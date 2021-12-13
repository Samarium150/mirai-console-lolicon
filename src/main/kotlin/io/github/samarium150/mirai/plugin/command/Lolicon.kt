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
package io.github.samarium150.mirai.plugin.command

import io.github.samarium150.mirai.plugin.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.config.CommandConfig
import io.github.samarium150.mirai.plugin.config.ExecutionConfig
import io.github.samarium150.mirai.plugin.config.PluginConfig
import io.github.samarium150.mirai.plugin.config.ReplyConfig
import io.github.samarium150.mirai.plugin.data.PluginData
import io.github.samarium150.mirai.plugin.data.RequestBody
import io.github.samarium150.mirai.plugin.data.ResponseBody
import io.github.samarium150.mirai.plugin.util.RequestHandler
import io.github.samarium150.mirai.plugin.util.Timer
import io.github.samarium150.mirai.plugin.util.Utils
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
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

/**
 * 命令实例
 *
 * @constructor 实例化命令
 * @see net.mamoe.mirai.console.command.CompositeCommand
 */
object Lolicon : CompositeCommand(
    MiraiConsoleLolicon,
    primaryName = "lolicon",
    secondaryNames = CommandConfig.lolicon
) {

    private val help: String
    private val logger = MiraiConsoleLolicon.logger

    /**
     * 忽略命令前缀
     */
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    /**
     * 初始化时从文本文件读取帮助信息
     */
    init {
        val helpFileName = "help.txt"
        help = javaClass.classLoader.getResource("help.txt")?.readText() ?: throw Exception("没有找到 $helpFileName 文件")
    }

    /**
     * 子命令get，根据 [tagArgs] 从API获取图片
     *
     * @param tagArgs 关键词
     */
    @OptIn(DelicateCoroutinesApi::class)
    @SubCommand("get", "来一张")
    @Description("根据标签发送涩图, 不提供则随机发送一张")
    suspend fun CommandSender.get(vararg tagArgs: String) {
        if (!Utils.isPermitted(subject, user)) {
            val where = if (subject is Group) "@${(subject as Group).id}" else ""
            logger.info("当前模式为'${PluginConfig.mode}'，${user?.id}${where}的命令已被无视")
            return
        }
        if (!Timer.getCooldown(subject)) {
            sendMessage(ReplyConfig.inCooldown)
            return
        }
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val tags = tagArgs.joinToString(" ")
        val body = if (tags.isNotEmpty())
            RequestBody(
                r18, 1, listOf(), "", Utils.processTags(tags),
                listOf(PluginConfig.size), PluginConfig.proxy
            )
        else RequestBody(r18, 1, listOf(), tags, listOf(), listOf(PluginConfig.size), PluginConfig.proxy)
        logger.info(body.toString())
        val response: ResponseBody?
        try {
            response = RequestHandler.get(body)
        } catch (e: Exception) {
            logger.error(e)
            return
        }
        logger.info(response.toString())
        if (response.error.isNotEmpty()) {
            sendMessage(ReplyConfig.invokeException)
            logger.warning(response.error)
            return
        }
        if (response.data.isEmpty()) {
            sendMessage(ReplyConfig.emptyImageData)
            return
        }
        try {
            val imageData = response.data[0]
            if (!Utils.areTagsAllowed(imageData.tags)) {
                sendMessage(ReplyConfig.filteredTag)
                return
            }
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
                                if (!result) logger.warning(imgReceipt.target.toString() + "图片撤回失败")
                                else logger.info(imgReceipt.target.toString() + "图片已撤回")
                            }
                        }
                    if (cooldown > 0) {
                        Timer.setCooldown(subject)
                        GlobalScope.launch {
                            Timer.cooldown(subject, cooldown)
                            withContext(Dispatchers.Default) {
                                logger.info(imgReceipt.target.toString() + "命令已冷却")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error(e)
                sendMessage(ReplyConfig.networkError)
            } finally {
                @Suppress("BlockingMethodInNonBlockingContext")
                stream?.close()
                if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                    GlobalScope.launch {
                        val result = imgInfoReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                        withContext(Dispatchers.Default) {
                            if (!result) logger.warning(imgInfoReceipt.target.toString() + "图片信息撤回失败")
                            else logger.info(imgInfoReceipt.target.toString() + "图片信息已撤回")
                        }
                    }
            }
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    /**
     * 子命令adv，根据 [jsonArgs] 从API获取图片
     *
     * @param jsonArgs JSON字符串
     */
    @Suppress("unused")
    @OptIn(DelicateCoroutinesApi::class, ExperimentalSerializationApi::class)
    @SubCommand("adv", "高级")
    @Description("根据JSON字符串发送涩图")
    suspend fun CommandSender.advanced(vararg jsonArgs: String) {
        if (!Utils.isPermitted(subject, user)) {
            val where = if (subject is Group) "@${(subject as Group).id}" else ""
            logger.info("当前模式为'${PluginConfig.mode}'，${user?.id}${where}的命令已被无视")
            return
        }
        val json = jsonArgs.joinToString(" ")
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val body: RequestBody?
        try {
            body = Json.decodeFromString<RequestBody>(json)
        } catch (e: Exception) {
            sendMessage(ReplyConfig.invalidJson)
            logger.warning(e)
            return
        }
        logger.info(body.toString())
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
            logger.error(e)
            return
        }
        logger.info(response.toString())
        if (response.error.isNotEmpty()) {
            sendMessage(ReplyConfig.invokeException)
            logger.warning(response.error)
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
                    logger.error(e)
                    sendMessage(ReplyConfig.networkError)
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
                            if (!result) logger.warning(imgReceipt.target.toString() + "图片撤回失败")
                            else logger.info(imgReceipt.target.toString() + "图片已撤回")
                        }
                    }
                if (cooldown > 0) {
                    Timer.setCooldown(subject)
                    GlobalScope.launch {
                        Timer.cooldown(subject, cooldown)
                        withContext(Dispatchers.Default) {
                            logger.info(imgReceipt.target.toString() + "命令已冷却")
                        }
                    }
                }
            }
            if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                GlobalScope.launch {
                    val result = imgInfoReceipt.recallIn((recall * 1000).toLong()).awaitIsSuccess()
                    withContext(Dispatchers.Default) {
                        if (!result) logger.warning(imgInfoReceipt.target.toString() + "图片信息撤回失败")
                        else logger.info(imgInfoReceipt.target.toString() + "图片信息已撤回")
                    }
                }
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    /**
     * 子命令set，设置 [property] 和对应的 [value]
     *
     * @param property 目标属性
     * @param value 对应值
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
     * 子命令add, 添加用户id或群组id到对应的集合，用于黑白名单
     *
     * @param type group/user
     * @param id 目标ID
     */
    @Suppress("unused")
    @SubCommand("add", "添加")
    @Description("添加用户id或群组id到对应的集合，用于黑白名单")
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
     * 子命令trust，将用户添加到受信任名单
     *
     * @param id 目标ID
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
     * 子命令distrust，将用户从受信任名单中移除
     *
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
     * 子命令reload，重新载入插件配置和数据
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
        MiraiConsoleLolicon.reloadPluginConfig(PluginConfig)
        MiraiConsoleLolicon.reloadPluginData(PluginData)
        sendMessage(ReplyConfig.reloaded)
    }

    /**
     * 子命令help，获取帮助信息
     */
    @Suppress("unused")
    @SubCommand("help", "帮助")
    @Description("获取帮助信息")
    suspend fun CommandSender.help() {
        sendMessage(help)
    }
}
