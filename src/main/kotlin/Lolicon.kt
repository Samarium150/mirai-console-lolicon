package com.github.samarium150.mirai.plugin

import kotlinx.coroutines.*
import net.mamoe.mirai.Mirai
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.utils.sendImage
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
    private suspend fun recallAsync(receipt: MessageReceipt<Contact>) = GlobalScope.async(start = CoroutineStart.LAZY) {
        delay(30000L)
        try {
            Mirai.recallMessage(receipt.target.bot, receipt.source)
        } catch (e: Exception) {
            return@async false
        }
        return@async true
    }

    /**
     * Subcommand get, get image according to [keyword]
     *
     * @receiver [CommandSender]
     * @param keyword [String]
     */
    @SubCommand("get")
    @Description("根据关键字发送涩图, 不提供关键字则随机发送一张")
    suspend fun CommandSender.get(keyword: String = "") {
        val r18 = if (this.subject is User) false else PluginData.r18Groups.contains(this.subject?.id)
        val key: String
        val id = this.subject?.id
        key = if (id == null) Config.key
        else if (this.subject is User && PluginData.customAPIKeyUsers.contains(id))
            PluginData.customAPIKeyUsers.getValue(id)
        else if (this.subject is Group && PluginData.customAPIKeyGroups.contains(id))
            PluginData.customAPIKeyGroups.getValue(id)
        else Config.key
        val request = Request(key, keyword, r18)
        Main.logger.info(request.toReadable())
        try {
            val response: Response = RequestHandler.get(request)
            Main.logger.info(response.toReadable())
            for (imageData in response.data) {
                val receipt = this.subject?.sendImage(RequestHandler.download(imageData.url))
                sendMessage(imageData.toReadable())
                if (receipt != null) {
                    coroutineScope {
                        val result = recallAsync(receipt).await()
                        withContext(Dispatchers.Default) {
                            if (!result) Main.logger.warning("撤回失败")
                            else Main.logger.info("图片已撤回")
                        }
                    }
                }
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
                if (this.subject is Group) {
                    if (value.toBoolean()) {
                        PluginData.r18Groups.add((this.subject as Group).id)
                        sendMessage("设置成功")
                    } else {
                        if (PluginData.r18Groups.remove((this.subject as Group).id))
                            sendMessage("设置成功")
                    }
                }
            }
            "apikey" -> {
                when (this.subject) {
                    is User -> {
                        val id = (this.subject as User).id
                        if (value.toLowerCase() == "default") PluginData.customAPIKeyUsers.remove(id)
                        else PluginData.customAPIKeyUsers[id] = value
                    }
                    is Group -> {
                        val id = (this.subject as Group).id
                        if (value.toLowerCase() == "default") PluginData.customAPIKeyGroups.remove(id)
                        else PluginData.customAPIKeyGroups[id] = value
                    }
                    else -> Config.key = value
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