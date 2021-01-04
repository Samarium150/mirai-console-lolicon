package com.github.samarium150.mirai.plugin

import kotlinx.coroutines.*
import kotlinx.io.errors.IOException
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.MessageReceipt
import java.net.URL

/**
 * Command instance
 */
object Lolicon: CompositeCommand(
    Main, primaryName = "lolicon",
    secondaryNames = arrayOf("llc")
) {
    /**
     * Help information
     */
    private val help: String

    /**
     * Read help info from text when initializing
     */
    init {
        val helpFileName = "help.txt"
        val helpFile: URL? = javaClass.classLoader.getResource(helpFileName)
        if (helpFile != null) {
            help = helpFile.readText()
        } else throw Exception("$helpFileName does not found")
    }

    /**
     * Asynchronously recall the image sent by the bot
     *
     * @param receipt [MessageReceipt]<[Contact]> Receipt of sending the image
     * @return [Deferred]<[Boolean]> The result of the recall
     */
    private suspend fun recallAsync(receipt: MessageReceipt<Contact>) = GlobalScope.async(
        start = CoroutineStart.LAZY
    ) {
        delay(30000L)
        try {
            Mirai.recallMessage(receipt.target.bot, receipt.source)
        } catch (e: Exception) {
            return@async false
        }
        return@async true
    }

    /**
     * Load necessary information
     *
     * @param subject [Contact] Who sent the command
     * @return [Triple]<[String], [Int], [Int]> apikey, r18 property and cooldown time
     */
    private fun getConfigAndData(subject: Contact?): Triple<String, Int, Int> {
        val apikey: String
        val r18: Int
        val cooldown: Int
        when (subject) {
            is User -> {
                apikey = PluginData.customAPIKeyUsers[subject.id] ?: Config.apikey
                r18 = 0
                cooldown = PluginData.customCooldownUsers[subject.id] ?: Config.cooldown
            }
            is Group -> {
                apikey = PluginData.customAPIKeyGroups[subject.id] ?: Config.apikey
                r18 = PluginData.customR18Groups[subject.id] ?: 0
                cooldown = PluginData.customCooldownGroups[subject.id] ?: Config.cooldown
            }
            else -> {
                apikey = Config.apikey
                r18 = 0
                cooldown = Config.cooldown
            }
        }
        return Triple(apikey, r18, cooldown)
    }

    /**
     * Subcommand get, get image according to [keyword]
     *
     * @receiver [CommandSender]
     * @param keyword [String]
     */
    @SubCommand("get")
    @Description("(默认冷却时间60s)根据关键字发送涩图, 不提供关键字则随机发送一张")
    suspend fun CommandSender.get(keyword: String = "") {
        if (!Timer.getCooldown(subject)) {
            sendMessage("你怎么冲得到处都是")
            return
        }
        val (apikey, r18, cooldown) = getConfigAndData(subject)
        val request = Request(apikey, keyword, r18)
        Main.logger.info(request.toReadable())
        try {
            val response: Response = RequestHandler.get(request)
            Main.logger.info(response.toReadable())
            for (imageData in response.data) {
                val stream = RequestHandler.download(imageData.url)
                val receipt = subject?.sendImage(stream)
                sendMessage(imageData.toReadable())
                if (receipt != null) {
                   Timer.setCooldown(subject)
                    GlobalScope.launch {
                        val result = recallAsync(receipt).await()
                        withContext(Dispatchers.Default) {
                            if (!result) Main.logger.warning(receipt.target.toString()+"撤回失败")
                            else Main.logger.info(receipt.target.toString()+"图片已撤回")
                        }
                    }
                    GlobalScope.launch {
                        Timer.cooldown(subject, cooldown)
                        withContext(Dispatchers.Default) {
                            Main.logger.info(receipt.target.toString()+"命令已冷却")
                        }
                    }
                }
                @Suppress("BlockingMethodInNonBlockingContext")
                withContext(Dispatchers.IO) { try { stream.close() } catch (e: IOException) {} }
            }
        } catch (e: Exception) {
            Main.logger.error(e)
        }
    }

    /**
     * Subcommand set, set [property] and its [value]
     *
     * @receiver [CommandSender]
     * @param property [String]
     * @param value [String]
     */
    @SubCommand("set")
    @Description("设置属性, 详见帮助信息")
    suspend fun CommandSender.set(property: String, value: String) {
        when(property) {
            "r18" -> {
                if (subject is Group) {
                    val setting: Int
                    try {
                        setting = value.toInt()
                    } catch (e: NumberFormatException) {
                        sendMessage("非法属性")
                        return
                    }
                    if (setting != 0 || setting != 1 || setting != 2) {
                        sendMessage("非法属性")
                        return
                    }
                    PluginData.customR18Groups[(subject as Group).id] = setting
                    sendMessage("设置成功")
                }
            }
            "apikey" -> {
                when (subject) {
                    is User -> {
                        val id = (subject as User).id
                        if (value.toLowerCase() == "default") PluginData.customAPIKeyUsers.remove(id)
                        else PluginData.customAPIKeyUsers[id] = value
                    }
                    is Group -> {
                        val id = (subject as Group).id
                        if (value.toLowerCase() == "default") PluginData.customAPIKeyGroups.remove(id)
                        else PluginData.customAPIKeyGroups[id] = value
                    }
                    else -> Config.apikey = value
                }
                sendMessage("设置成功")
            }
            "cooldown" -> {
                val setting: Int
                try {
                    setting = value.toInt()
                } catch (e: NumberFormatException) {
                    sendMessage("非法属性")
                    return
                }
                when (subject) {
                    is User -> PluginData.customCooldownUsers[(subject as User).id] = setting
                    is Group -> PluginData.customCooldownGroups[(subject as Group).id] = setting
                    else -> Config.cooldown = setting
                }
                sendMessage("设置成功")
            }
            else -> {
                sendMessage("非法属性")
            }
        }
    }

    /**
     * SubCommand help, send back help information
     *
     * @receiver [CommandSender]
     */
    @SubCommand("help")
    @Description("获取帮助信息")
    suspend fun CommandSender.help() {
        sendMessage(help)
    }
}