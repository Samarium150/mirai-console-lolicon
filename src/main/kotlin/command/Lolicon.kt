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
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.Permission
import net.mamoe.mirai.console.permission.PermissionService
import net.mamoe.mirai.console.permission.PermissionService.Companion.hasPermission
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.contact.Contact.Companion.uploadImage
import net.mamoe.mirai.event.events.FriendMessageEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.message.data.FlashImage
import net.mamoe.mirai.message.data.ForwardMessageBuilder
import net.mamoe.mirai.message.data.MessageChainBuilder
import net.mamoe.mirai.message.data.PlainText
import java.io.InputStream

object Lolicon : CompositeCommand(
    MiraiConsoleLolicon,
    primaryName = "lolicon",
    secondaryNames = CommandConfig.lolicon,
    description = "Lolicon发图命令"
) {

    val trusted: Permission by lazy {
        PermissionService.INSTANCE.register(
            MiraiConsoleLolicon.permissionId("trusted"),
            "受信任权限",
            MiraiConsoleLolicon.parentPermission
        )
    }

    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional: Boolean = true

    @SubCommand("get", "来一张")
    @Description("根据标签发送涩图, 不提供则随机发送一张")
    suspend fun CommandSender.get(tags: String = "") {
        val mutex = getSubjectMutex(subject) ?: return
        if (mutex.isLocked) {
            logger.info("throttled")
            return
        }
        mutex.withLock {
            val (r18, recall, cooldown) = ExecutionConfig(subject)
            val body = if (tags.isNotEmpty())
                RequestBody(
                    r18, 1, listOf(), "", processTags(tags),
                    listOf(PluginConfig.size.name.lowercase()), PluginConfig.proxy
                )
            else RequestBody(
                r18, 1, listOf(), tags, listOf(),
                listOf(PluginConfig.size.name.lowercase()), PluginConfig.proxy
            )
            logger.info("request body: $body")
            val notificationReceipt = getNotificationReceipt()
            val response = processRequest(body) ?: return@withLock
            val imageData = response.data[0]
            if (!areTagsAllowed(imageData.tags)) {
                sendMessage(ReplyConfig.filteredTag)
                return@withLock
            }
            val url = imageData.urls[PluginConfig.size.name.lowercase()] ?: return@withLock
            val imgInfoReceipt =
                if (subject == null ||
                    PluginConfig.verbose && PluginConfig.messageType != PluginConfig.Type.Forward
                ) sendMessage(imageData.toReadable(url))
                else null
            if (subject == null && !PluginConfig.save)
                return@withLock
            val stream: InputStream?
            try {
                stream = getImageInputStream(url)
            } catch (e: Exception) {
                logger.error(e)
                sendMessage(ReplyConfig.networkError)
                return@withLock
            }
            if (subject == null) {
                runInterruptible(Dispatchers.IO) {
                    stream.close()
                }
                return@withLock
            }
            val image = (subject as Contact).uploadImage(stream)
            val imgReceipt = sendMessage(buildMessage(subject as Contact, imageData.toReadable(url), image))
            if (notificationReceipt != null)
                recall(RecallType.NOTIFICATION, notificationReceipt, 0)
            if (imgReceipt == null)
                return@withLock
            else if (recall > 0 && PluginConfig.recallImg)
                recall(RecallType.IMAGE, imgReceipt, recall)
            if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                recall(RecallType.IMAGE_INFO, imgInfoReceipt, recall)
            if (cooldown > 0)
                cooldown(subject, cooldown)
            runInterruptible(Dispatchers.IO) {
                stream.close()
            }
        }
    }

    @SubCommand("adv", "高级")
    @Description("根据JSON字符串发送涩图")
    suspend fun CommandSender.advanced(json: String) {
        val mutex = getSubjectMutex(subject) ?: return
        if (mutex.isLocked) {
            logger.info("throttled")
            return
        }
        mutex.withLock {
            val (r18, recall, cooldown) = ExecutionConfig(subject)
            val body: RequestBody = runCatching {
                Json.decodeFromString<RequestBody>(json)
            }.onFailure {
                sendMessage(ReplyConfig.invalidJson)
                logger.warning(it)
            }.getOrNull() ?: return@withLock
            logger.info(body.toString())
            val notificationReceipt = getNotificationReceipt()
            if (body.r18 != r18) {
                if (subject is Group && !(user as Member).isOperator()) {
                    sendMessage(ReplyConfig.nonAdminPermissionDenied)
                    return@withLock
                }
                if (subject is User && !this.hasPermission(trusted)) {
                    sendMessage(ReplyConfig.untrusted)
                    return@withLock
                }
            }
            val response = processRequest(body) ?: return@withLock
            if (subject != null && PluginConfig.messageType == PluginConfig.Type.Forward) {
                val contact = subject as Contact
                val imageMsgBuilder = ForwardMessageBuilder(contact)
                imageMsgBuilder.displayStrategy = CustomDisplayStrategy
                for (imageData in response.data) {
                    when {
                        imageData.urls.size > 1 -> {
                            imageMsgBuilder.add(contact.bot, PlainText(imageData.toReadable(imageData.urls)))
                            for (url in imageData.urls.values) {
                                runCatching {
                                    val stream = getImageInputStream(url)
                                    val image = contact.uploadImage(stream)
                                    imageMsgBuilder.add(contact.bot, image)
                                    stream
                                }.onFailure {
                                    logger.error(it)
                                    imageMsgBuilder.add(contact.bot, PlainText(ReplyConfig.networkError))
                                }.onSuccess {
                                    runInterruptible(Dispatchers.IO) {
                                        it.close()
                                    }
                                }
                            }
                        }

                        imageData.urls.size == 1 -> {
                            runCatching {
                                val stream = getImageInputStream(imageData.urls.values.first())
                                val image = contact.uploadImage(stream)
                                imageMsgBuilder.add(contact.bot, PlainText(imageData.toReadable(imageData.urls)))
                                imageMsgBuilder.add(contact.bot, image)
                                stream
                            }.onFailure {
                                logger.error(it)
                                imageMsgBuilder.add(contact.bot, PlainText(imageData.toReadable(imageData.urls)))
                                imageMsgBuilder.add(contact.bot, PlainText(ReplyConfig.networkError))
                            }.onSuccess {
                                runInterruptible(Dispatchers.IO) {
                                    it.close()
                                }
                            }
                        }

                        else -> {
                            continue
                        }
                    }
                }
                val imgReceipt = sendMessage(imageMsgBuilder.build())
                if (notificationReceipt != null)
                    recall(RecallType.NOTIFICATION, notificationReceipt, 0)
                if (imgReceipt == null) {
                    return@withLock
                } else if (recall > 0 && PluginConfig.recallImg)
                    recall(RecallType.IMAGE, imgReceipt, recall)
                if (cooldown > 0)
                    cooldown(subject, cooldown)
            } else {
                val imageInfoMsgBuilder = MessageChainBuilder()
                val imageMsgBuilder = MessageChainBuilder()
                for (imageData in response.data) {
                    when {
                        imageData.urls.size > 1 -> {
                            for (url in imageData.urls.values) {
                                runCatching {
                                    val stream = getImageInputStream(url)
                                    val image = subject?.uploadImage(stream)
                                    if (image != null)
                                        if (PluginConfig.messageType == PluginConfig.Type.Flash)
                                            imageMsgBuilder.add(FlashImage(image))
                                        else
                                            imageMsgBuilder.add(image)
                                    stream
                                }.onFailure {
                                    logger.error(it)
                                    sendMessage(ReplyConfig.networkError)
                                }.onSuccess {
                                    imageInfoMsgBuilder.add(imageData.toReadable(imageData.urls))
                                    imageInfoMsgBuilder.add("\n")
                                    runInterruptible(Dispatchers.IO) {
                                        it.close()
                                    }
                                }
                            }
                        }

                        imageData.urls.size == 1 -> runCatching {
                            val stream = getImageInputStream(imageData.urls.values.first())
                            val image = subject?.uploadImage(stream)
                            if (image != null)
                                if (PluginConfig.messageType == PluginConfig.Type.Flash)
                                    imageMsgBuilder.add(FlashImage(image))
                                else
                                    imageMsgBuilder.add(image)
                            stream
                        }.onFailure {
                            logger.error(it)
                            sendMessage(ReplyConfig.networkError)
                        }.onSuccess {
                            imageInfoMsgBuilder.add(imageData.toReadable(imageData.urls))
                            imageInfoMsgBuilder.add("\n")
                            runInterruptible(Dispatchers.IO) {
                                it.close()
                            }
                        }

                        else -> {
                            continue
                        }
                    }
                }
                val imgInfoReceipt =
                    if (subject == null || PluginConfig.verbose)
                        sendMessage(imageInfoMsgBuilder.asMessageChain())
                    else null
                val imgReceipt = sendMessage(imageMsgBuilder.build())
                if (notificationReceipt != null)
                    recall(RecallType.NOTIFICATION, notificationReceipt, 0)
                if (imgReceipt == null) {
                    return@withLock
                } else if (recall > 0 && PluginConfig.recallImg)
                    recall(RecallType.IMAGE, imgReceipt, recall)
                if (PluginConfig.verbose && imgInfoReceipt != null && recall > 0 && PluginConfig.recallImgInfo)
                    recall(RecallType.IMAGE_INFO, imgInfoReceipt, recall)
                if (cooldown > 0)
                    cooldown(subject, cooldown)
            }
        }
    }

    @SubCommand("set", "设置")
    @Description("设置属性, 详见帮助信息")
    suspend fun CommandSenderOnMessage<MessageEvent>.set(property: PluginData.Property, value: Int) {
        if (fromEvent !is GroupMessageEvent && fromEvent !is FriendMessageEvent)
            return
        if (fromEvent is GroupMessageEvent && !(fromEvent as GroupMessageEvent).sender.isOperator()) {
            sendMessage(ReplyConfig.nonAdminPermissionDenied)
            return
        }
        if (fromEvent is FriendMessageEvent && !this.hasPermission(trusted)) {
            sendMessage(ReplyConfig.untrusted)
            return
        }
        logger.info("set $property to $value")
        if (value < 0) {
            sendMessage(value.toString() + ReplyConfig.illegalValue)
            return
        }
        if (setProperty(fromEvent.subject, property, value))
            sendMessage(ReplyConfig.setSucceeded)
        else
            sendMessage(value.toString() + ReplyConfig.illegalValue)
    }
}
