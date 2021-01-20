/**
 * Copyright (c) 2020-2021 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package com.github.samarium150.mirai.plugin

import com.github.kittinunf.fuel.core.FuelError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
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
     * Subcommand get, get the image according to [keyword]
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
        val (apikey, r18, recall, cooldown) = ExecutionConfig.create(subject)
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
                    if (recall > 0) {
                        GlobalScope.launch {
                            val result = receipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                            withContext(Dispatchers.Default) {
                                if (!result) Main.logger.warning(receipt.target.toString() + "撤回失败")
                                else Main.logger.info(receipt.target.toString() + "图片已撤回")
                            }
                        }
                    }
                    if (cooldown > 0) {
                        Timer.setCooldown(subject)
                        GlobalScope.launch {
                            Timer.cooldown(subject, cooldown)
                            withContext(Dispatchers.Default) {
                                Main.logger.info(receipt.target.toString()+"命令已冷却")
                            }
                        }
                    }
                }
                @Suppress("BlockingMethodInNonBlockingContext")
                stream.close()
            }
        } catch (fe: FuelError) {
            Main.logger.warning(fe.toString())
            sendMessage("网络连接失败或图片已被删除，之后再试试吧")
        } catch (ae: APIError) {
            Main.logger.warning(ae.toString())
            sendMessage(ae.toReadable())
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
        if (subject is Group && !Utils.checkMemberPerm(user)) {
            sendMessage("set仅限群主和管理员操作")
            return
        }
        when (property) {
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
                    else -> PluginConfig.apikey = value
                }
                sendMessage("设置成功")
            }
            "r18" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "r18")
                } catch (e: NumberFormatException) {
                    sendMessage("${value}不是有效的数字")
                    return
                }
                when (subject) {
                    is User -> PluginData.customR18Users[(subject as User).id] = setting
                    is Group -> PluginData.customR18Groups[(subject as Group).id] = setting
                }
                sendMessage("设置成功")
            }
            "recall" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "recall")
                } catch (e: NumberFormatException) {
                    sendMessage("${value}不是有效的数字")
                    return
                }
                when (subject) {
                    is User -> PluginData.customRecallUsers[(subject as User).id] = setting
                    is Group -> PluginData.customRecallGroups[(subject as Group).id] = setting
                    else -> PluginConfig.recall = setting
                }
                sendMessage("设置成功")
            }
            "cooldown" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "cooldown")
                } catch (e: NumberFormatException) {
                    sendMessage("${value}不是有效的数字")
                    return
                }
                when (subject) {
                    is User -> PluginData.customCooldownUsers[(subject as User).id] = setting
                    is Group -> PluginData.customCooldownGroups[(subject as Group).id] = setting
                    else -> PluginConfig.cooldown = setting
                }
                sendMessage("设置成功")
            }
            else -> {
                sendMessage("非法属性")
            }
        }
    }

    /**
     * Subcommand trust, add user to trusted set
     *
     * @receiver [CommandSender]
     * @param id [Long]
     */
    @SubCommand("trust")
    @Description("将用户添加到受信任名单")
    suspend fun CommandSender.trust(id: Long) {
        if (!Utils.checkMaster(user)) {
            sendMessage("该命令仅能由Bot所有者使用")
            if (PluginConfig.master == 0L) sendMessage("请先在配置文件设置Bot所有者id")
            return
        }
        if (PluginData.trustedUsers.add(id))
            sendMessage("添加成功")
        else
            sendMessage("该用户已经在名单中")
    }

    /**
     * Subcommand distrust, remove user from trusted set
     *
     * @receiver [CommandSender]
     * @param id [Long]
     */
    @SubCommand("distrust")
    @Description("将用户从受信任名单中移除")
    suspend fun CommandSender.distrust(id: Long) {
        if (!Utils.checkMaster(user)) {
            sendMessage("该命令仅能由Bot所有者使用")
            if (PluginConfig.master == 0L) sendMessage("请先在配置文件设置Bot所有者id")
            return
        }
        if (id == PluginConfig.master) {
            sendMessage("Bot所有者不能被移除")
            return
        }
        if (PluginData.trustedUsers.remove(id))
            sendMessage("移除成功")
        else
            sendMessage("该用户不在名单中")
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
