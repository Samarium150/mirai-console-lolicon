/*
 * Copyright (c) 2020-2023 Samarium
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
package io.github.samarium150.mirai.plugin.lolicon.util

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.config.ReplyConfig
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import io.github.samarium150.mirai.plugin.lolicon.data.RequestBody
import io.github.samarium150.mirai.plugin.lolicon.data.ResponseBody
import kotlinx.coroutines.sync.Mutex
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSenderOnMessage
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.User
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import net.mamoe.mirai.message.data.MessageSource.Key.quote
import java.io.File

internal val logger by lazy { MiraiConsoleLolicon.logger }

internal val cacheFolder: File by lazy {
    val folder = MiraiConsoleLolicon.dataFolder.resolve("download")
    if (PluginConfig.save && !folder.exists())
        folder.mkdirs()
    folder
}

internal suspend fun CommandSender.getSubjectMutex(subject: Contact?): Mutex? {
    if (getCooldownStatus(subject)) {
        sendMessage(ReplyConfig.inCooldown)
        return null
    }
    return getThrottleMutex(subject)
}

internal suspend fun CommandSender.getNotificationReceipt(): MessageReceipt<Contact>? {
    return if (PluginConfig.notify)
        if (subject != null)
            sendMessage((this as CommandSenderOnMessage<*>).fromEvent.source.quote() + ReplyConfig.notify)
        else sendMessage(ReplyConfig.notify)
    else null
}

internal suspend fun CommandSender.processRequest(body: RequestBody): ResponseBody? = runCatching {
    getAPIResponse(body)
}.fold(
    onSuccess = {
        logger.info(it.toString())
        if (it.error.isNotEmpty()) {
            sendMessage(ReplyConfig.invokeException)
            logger.warning(it.error)
            return null
        }
        if (it.data.isEmpty()) {
            sendMessage(ReplyConfig.emptyImageData)
            return null
        }
        it
    },
    onFailure = {
        logger.error(it)
        null
    }
)

internal fun processTags(str: String): List<List<String>> {
    val result: MutableList<List<String>> = listOf<List<String>>().toMutableList()
    val and = str.split("&")
    for (s in and) result.add(s.split("|"))
    return result.toList()
}

internal fun isTagAllowed(tag: String): Boolean {
    return when (PluginConfig.tagFilterMode) {
        PluginConfig.Mode.None -> true
        PluginConfig.Mode.Whitelist -> {
            for (filter in PluginConfig.tagFilter) {
                if (filter.toRegex(setOf(RegexOption.IGNORE_CASE)).matches(tag)) return true
            }
            false
        }

        PluginConfig.Mode.Blacklist -> {
            for (filter in PluginConfig.tagFilter) {
                if (filter.toRegex(setOf(RegexOption.IGNORE_CASE)).matches(tag)) return false
            }
            true
        }
    }
}

internal fun areTagsAllowed(tags: List<String>): Boolean {
    return when (PluginConfig.tagFilterMode) {
        PluginConfig.Mode.None -> true
        PluginConfig.Mode.Whitelist -> {
            var flag = false
            for (tag in tags) {
                if (isTagAllowed(tag)) {
                    flag = true
                    break
                }
            }
            flag
        }

        PluginConfig.Mode.Blacklist -> {
            var flag = true
            for (tag in tags) {
                if (!isTagAllowed(tag)) {
                    flag = false
                    break
                }
            }
            flag
        }
    }
}

internal fun buildMessage(contact: Contact, imageInfo: String, image: Image): SingleMessage {
    return when (PluginConfig.messageType) {
        PluginConfig.Type.Simple -> image
        PluginConfig.Type.Flash -> FlashImage(image)
        PluginConfig.Type.Forward -> buildForwardMessage(contact, CustomDisplayStrategy) {
            if (imageInfo.isNotEmpty())
                add(contact.bot, PlainText(imageInfo))
            add(contact.bot, image)
        }
    }
}

internal fun setProperty(subject: Contact, property: PluginData.Property, value: Int): Boolean {
    val target = (if (subject is User) when (property) {
        PluginData.Property.R18 -> {
            if (value > 2) {
                return false
            }
            PluginData.customR18Users
        }

        PluginData.Property.RECALL -> PluginData.customRecallUsers
        PluginData.Property.COOLDOWN -> PluginData.customCooldownUsers
    } else when (property) {
        PluginData.Property.R18 -> {
            if (value > 2) {
                return false
            }
            PluginData.customR18Groups
        }

        PluginData.Property.RECALL -> PluginData.customRecallGroups
        PluginData.Property.COOLDOWN -> PluginData.customCooldownGroups
    })
    target[subject.id] = value
    return true
}
