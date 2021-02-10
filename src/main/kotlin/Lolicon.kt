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
import net.mamoe.mirai.console.plugin.jvm.reloadPluginConfig
import net.mamoe.mirai.console.plugin.jvm.reloadPluginData
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.FlashImage
import java.net.URL

/**
 * Command instance
 * <br>
 * 命令实例
 *
 * @constructor Create a CompositeCommand instance <br> 实例化命令
 * @see net.mamoe.mirai.console.command.CompositeCommand
 */
object Lolicon: CompositeCommand(
    Main, primaryName = "lolicon",
    secondaryNames = arrayOf("llc")
) {
    /**
     * Help information
     * <br>
     * 帮助信息
     */
    private val help: String

    /**
     * Read help info from text when initializing
     * <br>
     * 初始化时从文本文件读取帮助信息文件
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
     * <br>
     * 子命令get，根据 [keyword] 从API获取图片
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param keyword keyword for searching <br> 关键词
     */
    @SubCommand("get")
    @Description("(默认冷却时间60s)根据关键字发送涩图, 不提供关键字则随机发送一张")
    suspend fun CommandSender.get(keyword: String = "") {
        if (!Timer.getCooldown(subject)) {
            sendMessage("你怎么冲得到处都是")
            return
        }
        val (apikey, r18, recall, cooldown) = ExecutionConfig.create(subject)
        val parameters = RequestParams(apikey, keyword, r18, size1200 = PluginConfig.size1200)
        Main.logger.info(parameters.toReadable())
        try {
            val response: Response = RequestHandler.get(parameters)
            Main.logger.info(response.toReadable())
            for (imageData in response.data) {
                Main.logger.info("url: ${imageData.url}")
                sendMessage(imageData.toReadable())
                val stream = RequestHandler.download(imageData.url)
                val img = subject?.uploadImage(stream)
                if (img != null) {
                    val receipt = (if (PluginConfig.flash) sendMessage(FlashImage(img)) else sendMessage(img)) ?: return
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
            sendMessage("网络连接失败/超时或图片已被删除，之后再试试吧")
        } catch (ae: APIException) {
            Main.logger.warning(ae.toString())
            sendMessage(ae.toReadable())
        } catch (e: Exception) {
            Main.logger.error(e)
        }
    }

    /**
     * Subcommand set, set [property] and its [value]
     * <br>
     * 子命令set，设置属性和对应的值
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param property target property <br> 目标属性
     * @param value corresponding value <br> 对应值
     * @see PluginConfig
     * @see PluginData
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
     * <br>
     * 子命令trust，将用户添加到受信任名单
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param id id of the target user <br> 目标QQ号
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
     * <br>
     * 子命令distrust，将用户从受信任名单中移除
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param id id of the target user <br> 目标QQ号
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
     * Subcommand reload, reload plugin configuration and data
     * <br>
     * 子命令reload，重新载入插件配置和数据
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     */
    @SubCommand("reload")
    @Description("重新载入插件配置和数据")
    suspend fun CommandSender.reload() {
        if (subject is Group && !Utils.checkMemberPerm(user)) {
            sendMessage("reload仅限群主和管理员操作")
            return
        } else if (!Utils.checkMaster(user)) {
            sendMessage("reload仅限Bot所有者使用")
            return
        }
        Main.reloadPluginConfig(PluginConfig)
        Main.reloadPluginData(PluginData)
        sendMessage("配置已重载")
    }

    /**
     * SubCommand help, send help information
     * <br>
     * 子命令help，获取帮助信息
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     */
    @SubCommand("help")
    @Description("获取帮助信息")
    suspend fun CommandSender.help() {
        sendMessage(help)
    }
}
