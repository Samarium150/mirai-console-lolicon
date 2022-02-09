/**
 * Copyright (c) 2020-2022 Samarium
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
package io.github.samarium150.mirai.plugin.lolicon.command

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.CommandConfig
import io.github.samarium150.mirai.plugin.lolicon.config.ExecutionConfig
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.config.ReplyConfig
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import io.github.samarium150.mirai.plugin.lolicon.data.RequestBody
import io.github.samarium150.mirai.plugin.lolicon.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.data.FlashImage
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import java.io.InputStream

/**
 * 命令实例
 *
 * @constructor 实例化命令
 * @see CompositeCommand
 */
object Lolicon : CompositeCommand(
    MiraiConsoleLolicon,
    primaryName = "lolicon",
    secondaryNames = CommandConfig.lolicon
) {

    /**
     * 帮助信息
     */
    private val help: String

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
    @SubCommand("get", "来一张")
    @Description("根据标签发送涩图, 不提供则随机发送一张")
    suspend fun CommandSender.get(vararg tagArgs: String) {
        if (!checkPermissionAndCooldown(this)) return
        if (!lock(subject)) {
            logger.info("throttled")
            return
        }
        if (PluginConfig.notify)
            sendMessage(ReplyConfig.notify)
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val tags = tagArgs.joinToString(" ")
        val body = if (tags.isNotEmpty())
            RequestBody(
                r18, 1, listOf(), "", processTags(tags),
                listOf(PluginConfig.size.name.lowercase()), PluginConfig.proxy
            )
        else RequestBody(
            r18,
            1,
            listOf(),
            tags,
            listOf(),
            listOf(PluginConfig.size.name.lowercase()),
            PluginConfig.proxy
        )
        logger.info("request body: $body")
        val response = processRequest(this, body)
        if (response == null) {
            unlock(subject)
            return
        }
        try {
            val imageData = response.data[0]
            if (!areTagsAllowed(imageData.tags)) {
                sendMessage(ReplyConfig.filteredTag)
                unlock(subject)
                return
            }
            val url = imageData.urls[PluginConfig.size.name.lowercase()]
            if (url == null) {
                unlock(subject)
                return
            }
            val imgInfoReceipt =
                if (subject == null ||
                    PluginConfig.verbose && PluginConfig.messageType != PluginConfig.Type.Forward)
                    sendMessage(imageData.toReadable())
                else null
            var stream: InputStream? = null
            try {
                stream = getImageInputStream(url)
                val img = subject?.uploadImage(stream)
                if (img != null) {
                    val imgReceipt = sendMessage(buildMessage(subject!!, imageData.toReadable(), img))
                    if (imgReceipt == null) {
                        unlock(subject)
                        return
                    } else if (recall > 0 && PluginConfig.recallImg)
                        recall(RecallType.IMAGE, imgReceipt, recall)
                    if (cooldown > 0)
                        cooldown(subject, cooldown)
                }
            } catch (e: Exception) {
                logger.error(e)
                sendMessage(ReplyConfig.networkError)
            } finally {
                withContext(Dispatchers.IO) { stream?.close() }
                if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                    recall(RecallType.IMAGE_INFO, imgInfoReceipt, recall)
            }
        } catch (e: Exception) {
            logger.error(e)
        } finally {
            unlock(subject)
        }
    }

    /**
     * 子命令adv，根据 [jsonArgs] 从API获取图片
     *
     * @param jsonArgs JSON字符串
     */
    @Suppress("unused")
    @OptIn(ExperimentalSerializationApi::class)
    @SubCommand("adv", "高级")
    @Description("根据JSON字符串发送涩图")
    suspend fun CommandSender.advanced(vararg jsonArgs: String) {
        if (!checkPermissionAndCooldown(this)) return
        if (!lock(subject)) {
            logger.info("throttled")
            return
        }
        if (PluginConfig.notify)
            sendMessage(ReplyConfig.notify)
        val json = jsonArgs.joinToString(" ")
        val (r18, recall, cooldown) = ExecutionConfig.create(subject)
        val body: RequestBody?
        try {
            body = Json.decodeFromString<RequestBody>(json)
        } catch (e: Exception) {
            unlock(subject)
            sendMessage(ReplyConfig.invalidJson)
            logger.warning(e)
            return
        }
        logger.info(body.toString())
        if (body.r18 != r18) {
            if (subject is Group && !checkMemberPerm(user)) {
                sendMessage(ReplyConfig.nonAdminPermissionDenied)
                unlock(subject)
                return
            }
            if (subject is User && !checkUserPerm(user)) {
                sendMessage(ReplyConfig.untrusted)
                unlock(subject)
                return
            }
        }
        val response = processRequest(this, body)
        if (response == null) {
            unlock(subject)
            return
        }
        try {
            if (subject != null && PluginConfig.messageType == PluginConfig.Type.Forward) {
                val contact = subject as Contact
                val imageMsgBuilder = ForwardMessageBuilder(contact)
                imageMsgBuilder.displayStrategy = CustomDisplayStrategy
                for (imageData in response.data) {
                    if (imageData.urls.isEmpty()) continue
                    val url = getUrl(imageData.urls) ?: continue
                    var stream: InputStream? = null
                    try {
                        stream = getImageInputStream(url)
                        val img = contact.uploadImage(stream)
                        imageMsgBuilder.add(contact.bot, PlainText(imageData.toReadable()))
                        imageMsgBuilder.add(contact.bot, img)
                    } catch (e: Exception) {
                        logger.error(e)
                        imageMsgBuilder.add(contact.bot, PlainText(imageData.toReadable()))
                        imageMsgBuilder.add(contact.bot, PlainText(ReplyConfig.networkError))
                    } finally {
                        withContext(Dispatchers.IO) { stream?.close() }
                    }
                }
                val imgReceipt = sendMessage(imageMsgBuilder.build())
                if (imgReceipt == null) {
                    unlock(subject)
                    return
                } else if (recall > 0 && PluginConfig.recallImg)
                    recall(RecallType.IMAGE, imgReceipt, recall)
                if (cooldown > 0)
                    cooldown(subject, cooldown)
            } else {
                val imageInfoMsgBuilder = MessageChainBuilder()
                val imageMsgBuilder = MessageChainBuilder()
                for (imageData in response.data) {
                    if (imageData.urls.isEmpty()) continue
                    val url = getUrl(imageData.urls) ?: continue
                    var stream: InputStream? = null
                    try {
                        stream = getImageInputStream(url)
                        val img = subject?.uploadImage(stream)
                        if (img != null)
                            if (PluginConfig.messageType == PluginConfig.Type.Flash)
                                imageMsgBuilder.add(FlashImage(img))
                            else
                                imageMsgBuilder.add(img)
                        imageInfoMsgBuilder.add(imageData.toReadable())
                        imageInfoMsgBuilder.add("\n")
                    } catch (e: Exception) {
                        logger.error(e)
                        sendMessage(ReplyConfig.networkError)
                    } finally {
                        withContext(Dispatchers.IO) { stream?.close() }
                    }
                }
                val imgInfoReceipt =
                    if (subject == null || PluginConfig.verbose)
                        sendMessage(imageInfoMsgBuilder.asMessageChain())
                    else null
                val imgReceipt = sendMessage(imageMsgBuilder.asMessageChain())
                if (imgReceipt == null) {
                    unlock(subject)
                    return
                } else if (recall > 0 && PluginConfig.recallImg)
                    recall(RecallType.IMAGE, imgReceipt, recall)
                if (cooldown > 0)
                    cooldown(subject, cooldown)
                if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                    recall(RecallType.IMAGE_INFO, imgInfoReceipt, recall)
            }
        } catch (e: Exception) {
            logger.error(e)
        } finally {
            unlock(subject)
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
        if (subject is Group && !checkMemberPerm(user)) {
            sendMessage(ReplyConfig.nonAdminPermissionDenied)
            return
        }
        when (property) {
            "r18" -> {
                if (subject is User && !checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = convertValue(value, "r18")
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
                if (subject is User && !checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = convertValue(value, "recall")
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
                if (subject is User && !checkUserPerm(user)) {
                    sendMessage(ReplyConfig.untrusted)
                    return
                }
                val setting: Int
                try {
                    setting = convertValue(value, "cooldown")
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
            else -> sendMessage(property + ReplyConfig.illegalProperty)
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
        if (!checkMaster(user)) {
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
        if (!checkMaster(user)) {
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
     * @param id 目标ID
     */
    @Suppress("unused")
    @SubCommand("distrust", "不信任")
    @Description("将用户从受信任名单中移除")
    suspend fun CommandSender.distrust(id: Long) {
        if (!checkMaster(user)) {
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
     * 子命令help，获取帮助信息
     */
    @Suppress("unused")
    @SubCommand("help", "帮助")
    @Description("获取帮助信息")
    suspend fun CommandSender.help() {
        sendMessage(help)
    }
}
