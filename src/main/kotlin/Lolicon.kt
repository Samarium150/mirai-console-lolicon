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

import kotlinx.coroutines.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.plugin.jvm.reloadPluginConfig
import net.mamoe.mirai.console.plugin.jvm.reloadPluginData
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.FlashImage
import net.mamoe.mirai.message.data.MessageChainBuilder
import java.io.File
import java.io.InputStream
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
    secondaryNames = CommandConfig.lolicon
) {
    /**
     * Help information
     * <br>
     * 帮助信息
     */
    private val help: String

    /**
     * set prefix to be optional
     * <br>
     * 忽略前缀
     */
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

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
        } else throw Exception("没有找到 $helpFileName 文件")
    }

    /**
     * Subcommand get, get the image according to [tags]
     * <br>
     * 子命令get，根据 [tags] 从API获取图片
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param tags keyword for searching <br> 关键词
     */
    @OptIn(DelicateCoroutinesApi::class)
    @SubCommand("get", "来一张")
    @Description("根据标签发送涩图, 不提供则随机发送一张")
    suspend fun CommandSenderOnMessage<*>.get(tags: String = "") {
        if (!Utils.isPermitted(subject, user)) {
            val where = if (subject is Group) "@${(subject as Group).id}" else ""
            Main.logger.info("当前模式为'${PluginConfig.mode}'，${user?.id}${where}的命令已被无视")
            return
        }
        if (!Timer.getCooldown(subject)) {
            sendMessage(ReplyConfig.inCooldown)
            return
        }
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val body = if (tags.isNotEmpty())
            RequestBody(r18, 1, listOf(), "", Utils.processTags(tags),
                listOf(PluginConfig.size), PluginConfig.proxy)
        else RequestBody(r18, 1, listOf(), tags, listOf(), listOf(PluginConfig.size), PluginConfig.proxy)
        Main.logger.info(body.toString())
        val response: ResponseBody?
        try {
            response = RequestHandler.get(body)
        } catch (e: Exception) {
            Main.logger.error(e)
            return
        }
        Main.logger.info(response.toString())
        if (response.error.isNotEmpty()) {
            sendMessage(ReplyConfig.invokeException)
            Main.logger.warning(response.error)
            return
        }
        if (response.data.isEmpty()) {
            sendMessage(ReplyConfig.emptyImageData)
            return
        }
        try {
            val imageData = response.data[0]
            val url = imageData.urls[PluginConfig.size] ?: return
            val imgInfoReceipt =
                if (PluginConfig.verbose || subject == null) sendMessage(imageData.toReadable())
                else null
            var stream: InputStream? = null
            try {
                stream = if (PluginConfig.save && PluginConfig.cache) {
                    try {
                        val paths = url.split("/")
                        val path = "/data/mirai-console-lolicon/download/${paths[paths.lastIndex]}"
                        val cache = File(System.getProperty("user.dir") + path)
                        if (cache.exists()) cache.inputStream() else RequestHandler.download(url)
                    } catch (e: Exception) {
                        RequestHandler.download(url)
                    }
                } else RequestHandler.download(url)
                val img = subject?.uploadImage(stream)
                if (img != null) {
                    val imgReceipt = (
                        if (PluginConfig.flash) sendMessage(FlashImage(img)) else sendMessage(img)
                        ) ?: return
                    if (recall > 0 && PluginConfig.recallImg)
                        GlobalScope.launch {
                            val result = imgReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                            withContext(Dispatchers.Default) {
                                if (!result) Main.logger.warning(imgReceipt.target.toString() + "图片撤回失败")
                                else Main.logger.info(imgReceipt.target.toString() + "图片已撤回")
                            }
                        }
                    if (cooldown > 0) {
                        Timer.setCooldown(subject)
                        GlobalScope.launch {
                            Timer.cooldown(subject, cooldown)
                            withContext(Dispatchers.Default) {
                                Main.logger.info(imgReceipt.target.toString()+"命令已冷却")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Main.logger.error(e)
            } finally {
                @Suppress("BlockingMethodInNonBlockingContext")
                stream?.close()
                if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                    GlobalScope.launch {
                        val result = imgInfoReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                        withContext(Dispatchers.Default) {
                            if (!result) Main.logger.warning(imgInfoReceipt.target.toString() + "图片信息撤回失败")
                            else Main.logger.info(imgInfoReceipt.target.toString() + "图片信息已撤回")
                        }
                    }
            }
        } catch (e: Exception) {
            Main.logger.error(e)
        }
    }

    /**
     * Advanced get
     * <br>
     * 子命令adv，根据 [json] 获取图片
     *
     * @param json JSON字符串
     */
    @Suppress("unused")
    @OptIn(DelicateCoroutinesApi::class, kotlinx.serialization.ExperimentalSerializationApi::class)
    @SubCommand("adv", "高级")
    @Description("根据JSON字符串发送涩图")
    suspend fun CommandSender.advanced(json: String) {
        if (!Utils.isPermitted(subject, user)) {
            val where = if (subject is Group) "@${(subject as Group).id}" else ""
            Main.logger.info("当前模式为'${PluginConfig.mode}'，${user?.id}${where}的命令已被无视")
            return
        }
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val body: RequestBody?
        try {
            body = Json.decodeFromString<RequestBody>(json)
        } catch (e: Exception) {
            sendMessage(ReplyConfig.invalidJson)
            Main.logger.warning(e)
            return
        }
        if (body.r18 != r18) {
            if (subject is Group && !Utils.checkMemberPerm(user)) {
                sendMessage(ReplyConfig.nonAdminPermissionDenied)
                return
            }
            if (subject is User && !Utils.checkUserPerm(user)) {
                sendMessage(ReplyConfig.untrusted)
                return
            }
        }
        val response: ResponseBody?
        try {
            response = RequestHandler.get(body)
        } catch (e: Exception) {
            Main.logger.error(e)
            return
        }
        Main.logger.info(response.toString())
        if (response.error.isNotEmpty()) {
            sendMessage(ReplyConfig.invokeException)
            Main.logger.warning(response.error)
            return
        }
        if (response.data.isEmpty()) {
            sendMessage(ReplyConfig.emptyImageData)
            return
        }
        try {
            val imageInfoMsgBuilder = MessageChainBuilder()
            val imageMsgBuilder = MessageChainBuilder()
            for (imageData in response.data) {
                if (imageData.urls.isEmpty()) continue
                val url = Utils.getUrl(imageData.urls) ?: continue
                var stream: InputStream? = null
                try {
                    stream = if (PluginConfig.save && PluginConfig.cache) {
                        try {
                            val paths = url.split("/")
                            val path = "/data/mirai-console-lolicon/download/${paths[paths.lastIndex]}"
                            val cache = File(System.getProperty("user.dir") + path)
                            if (cache.exists()) cache.inputStream() else RequestHandler.download(url)
                        } catch (e: Exception) {
                            RequestHandler.download(url)
                        }
                    } else RequestHandler.download(url)
                    val img = subject?.uploadImage(stream)
                    if (img != null)
                        if (PluginConfig.flash)
                            imageMsgBuilder.add(FlashImage(img))
                        else
                            imageMsgBuilder.add(img)
                    imageInfoMsgBuilder.add(imageData.toReadable())
                    imageInfoMsgBuilder.add("\n")
                } catch (e: Exception) {
                    Main.logger.error(e)
                } finally {
                    @Suppress("BlockingMethodInNonBlockingContext")
                    stream?.close()
                }
            }
            val imgInfoReceipt =
                if (PluginConfig.verbose || subject == null) sendMessage(imageInfoMsgBuilder.asMessageChain())
                else null
            val imgReceipt = sendMessage(imageMsgBuilder.asMessageChain())
            if (imgReceipt != null) {
                if (recall > 0 && PluginConfig.recallImg)
                    GlobalScope.launch {
                        val result = imgReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                        withContext(Dispatchers.Default) {
                            if (!result) Main.logger.warning(imgReceipt.target.toString() + "图片撤回失败")
                            else Main.logger.info(imgReceipt.target.toString() + "图片已撤回")
                        }
                    }
                if (cooldown > 0) {
                    Timer.setCooldown(subject)
                    GlobalScope.launch {
                        Timer.cooldown(subject, cooldown)
                        withContext(Dispatchers.Default) {
                            Main.logger.info(imgReceipt.target.toString()+"命令已冷却")
                        }
                    }
                }
            }
            if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                GlobalScope.launch {
                    val result = imgInfoReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                    withContext(Dispatchers.Default) {
                        if (!result) Main.logger.warning(imgInfoReceipt.target.toString() + "图片信息撤回失败")
                        else Main.logger.info(imgInfoReceipt.target.toString() + "图片信息已撤回")
                    }
                }
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
    @SubCommand("set", "设置")
    @Description("设置属性, 详见帮助信息")
    suspend fun CommandSender.set(property: String, value: String) {
        if (subject is Group && !Utils.checkMemberPerm(user)) {
            sendMessage(ReplyConfig.nonAdminPermissionDenied)
            return
        }
        when (property) {
            "r18" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "r18")
                } catch (e: NumberFormatException) {
                    sendMessage(value + ReplyConfig.illegalValue)
                    return
                }
                when (subject) {
                    is User -> PluginData.customR18Users[(subject as User).id] = setting
                    is Group -> PluginData.customR18Groups[(subject as Group).id] = setting
                }
                sendMessage(ReplyConfig.setSucceeded)
            }
            "recall" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "recall")
                } catch (e: NumberFormatException) {
                    sendMessage(value + ReplyConfig.illegalValue)
                    return
                }
                when (subject) {
                    is User -> PluginData.customRecallUsers[(subject as User).id] = setting
                    is Group -> PluginData.customRecallGroups[(subject as Group).id] = setting
                    else -> PluginConfig.recall = setting
                }
                sendMessage(ReplyConfig.setSucceeded)
            }
            "cooldown" -> {
                if (subject is User && !Utils.checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = Utils.convertValue(value, "cooldown")
                } catch (e: NumberFormatException) {
                    sendMessage(value + ReplyConfig.illegalValue)
                    return
                }
                when (subject) {
                    is User -> PluginData.customCooldownUsers[(subject as User).id] = setting
                    is Group -> PluginData.customCooldownGroups[(subject as Group).id] = setting
                    else -> PluginConfig.cooldown = setting
                }
                sendMessage(ReplyConfig.setSucceeded)
            }
            else -> {
                sendMessage(property + ReplyConfig.illegalProperty)
            }
        }
    }

    /**
     * Add user id or group id to corresponding set
     * <br>
     * 添加用户id或群组id到对应的集合，用于黑白名单
     *
     * @param type
     * @param id
     */
    @Suppress("unused")
    @SubCommand("add", "添加")
    suspend fun CommandSender.add(type: String, id: Long) {
        if (!Utils.checkMaster(user)) {
            sendMessage(ReplyConfig.nonMasterPermissionDenied)
            if (PluginConfig.master == 0L) sendMessage(ReplyConfig.noMasterID)
            return
        }
        val success: Boolean = when (type) {
            "user" -> {
                PluginData.userSet.add(id)
                true
            }
            "group" -> {
                PluginData.groupSet.add(id)
                true
            }
            else -> {
                sendMessage("类型错误")
                false
            }
        }
        if (success) sendMessage("添加成功")
    }

    /**
     * Subcommand trust, add user to trusted set
     * <br>
     * 子命令trust，将用户添加到受信任名单
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param id id of the target user <br> 目标QQ号
     */
    @Suppress("unused")
    @SubCommand("trust", "信任")
    @Description("将用户添加到受信任名单")
    suspend fun CommandSender.trust(id: Long) {
        if (!Utils.checkMaster(user)) {
            sendMessage(ReplyConfig.nonMasterPermissionDenied)
            if (PluginConfig.master == 0L) sendMessage(ReplyConfig.noMasterID)
            return
        }
        if (PluginData.trustedUsers.add(id))
            sendMessage(ReplyConfig.trustSucceeded)
        else
            sendMessage(ReplyConfig.alreadyExists)
    }

    /**
     * Subcommand distrust, remove user from trusted set
     * <br>
     * 子命令distrust，将用户从受信任名单中移除
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     * @param id id of the target user <br> 目标QQ号
     */
    @Suppress("unused")
    @SubCommand("distrust", "不信任")
    @Description("将用户从受信任名单中移除")
    suspend fun CommandSender.distrust(id: Long) {
        if (!Utils.checkMaster(user)) {
            sendMessage(ReplyConfig.nonMasterPermissionDenied)
            if (PluginConfig.master == 0L) sendMessage(ReplyConfig.noMasterID)
            return
        }
        if (id == PluginConfig.master) {
            sendMessage("Bot所有者不能被移除")
            return
        }
        if (PluginData.trustedUsers.remove(id))
            sendMessage(ReplyConfig.distrustSucceeded)
        else
            sendMessage(ReplyConfig.doesNotExists)
    }

    /**
     * Subcommand reload, reload plugin configuration and data
     * <br>
     * 子命令reload，重新载入插件配置和数据
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     */
    @Suppress("unused")
    @SubCommand("reload", "重载")
    @Description("重新载入插件配置和数据")
    suspend fun CommandSender.reload() {
        if (subject is Group && !Utils.checkMemberPerm(user) && !Utils.checkMaster(user)) {
            sendMessage(ReplyConfig.nonAdminPermissionDenied)
            return
        } else if (subject is User && !Utils.checkMaster(user)) {
            sendMessage(ReplyConfig.nonMasterPermissionDenied)
            return
        }
        Main.reloadPluginConfig(PluginConfig)
        Main.reloadPluginData(PluginData)
        sendMessage(ReplyConfig.reloaded)
    }

    /**
     * SubCommand help, send help information
     * <br>
     * 子命令help，获取帮助信息
     *
     * @receiver [CommandSender] Command sender <br> 指令发送者
     */
    @Suppress("unused")
    @SubCommand("help", "帮助")
    @Description("获取帮助信息")
    suspend fun CommandSender.help() {
        sendMessage(help)
    }
}
