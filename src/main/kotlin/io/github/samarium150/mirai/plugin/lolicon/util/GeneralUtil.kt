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
package io.github.samarium150.mirai.plugin.lolicon.util

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.command.Lolicon
import io.github.samarium150.mirai.plugin.lolicon.command.Lolicon.set
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig.Type.*
import io.github.samarium150.mirai.plugin.lolicon.config.ReplyConfig
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import io.github.samarium150.mirai.plugin.lolicon.data.RequestBody
import io.github.samarium150.mirai.plugin.lolicon.data.ResponseBody
import io.ktor.client.features.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.ExperimentalSerializationApi
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.*
import net.mamoe.mirai.message.MessageReceipt
import net.mamoe.mirai.message.data.*
import org.jetbrains.annotations.Nullable
import java.io.File
import java.net.Proxy

internal val logger by lazy { MiraiConsoleLolicon.logger }

internal val cacheFolder: File by lazy {
    val folder = MiraiConsoleLolicon.dataFolder.resolve("download")
    if (PluginConfig.save && !folder.exists())
        folder.mkdirs()
    folder
}

/**
 * 检查用户是否是Bot所有者
 *
 * @param user 目标用户
 * @return 检查结果
 */
fun checkMaster(@Nullable user: User?): Boolean {
    return user == null || user.id == PluginConfig.master
}

/**
 * 检查用户是否受信任
 *
 * @param user 目标用户
 * @return 检查结果
 */
fun checkUserPerm(@Nullable user: User?): Boolean {
    return user == null || PluginData.trustedUsers.contains(user.id)
}

/**
 * 检查用户在群里的权限
 *
 * @param user 目标用户
 * @return 检查结果
 */
fun checkMemberPerm(@Nullable user: User?): Boolean {
    return (user as Member).permission != MemberPermission.MEMBER
}

/**
 * 将字符串转为整数值
 *
 * @param value 输入的字符串
 * @param type 需要转化的类别
 * @return 转换后的值
 * @throws NumberFormatException 数值非法时抛出
 * @see Lolicon.set
 */
@Throws(NumberFormatException::class)
fun convertValue(value: String, type: String): Int {
    val setting = value.toInt()
    when (type) {
        "r18" -> if (setting != 0 && setting != 1 && setting != 2) throw NumberFormatException(value)
        "recall" -> if (setting < 0 || setting >= 120) throw NumberFormatException(value)
        "cooldown" -> if (setting < 0) throw NumberFormatException(value)
    }
    return setting
}

/**
 * 根据输入值返回代理类型
 *
 * @param value 输入的字符串
 * @return 代理的类型
 * @throws IllegalArgumentException 数值非法时抛出
 * @see Proxy.Type
 */
@Throws(IllegalArgumentException::class)
fun getProxyType(value: String): Proxy.Type {
    return when (value) {
        "DIRECT" -> Proxy.Type.DIRECT
        "HTTP" -> Proxy.Type.HTTP
        "SOCKS" -> Proxy.Type.SOCKS
        else -> throw IllegalArgumentException(value)
    }
}

/**
 * 将字符串处理为标签列表
 *
 * @param str 输入的字符串
 * @return 标签列表
 */
fun processTags(str: String): List<List<String>> {
    val result: MutableList<List<String>> = listOf<List<String>>().toMutableList()
    val and = str.split("&")
    for (s in and) result.add(s.split("|"))
    return result.toList()
}

/**
 * 根据黑白名单检查标签是否合法
 *
 * @param tag 标签
 * @return 检查结果
 */
private fun isTagAllowed(tag: String): Boolean {
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

/**
 * 根据黑白名单检查标签列表中的标签是否合法
 *
 * @param tags 标签列表
 * @return 检查结果
 */
fun areTagsAllowed(tags: List<String>): Boolean {
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

private val sizeMap: Map<String, Int> = mapOf(
    "original" to 0,
    "regular" to 1,
    "small" to 2,
    "thumb" to 3,
    "mini" to 4
)

fun getUrl(urls: Map<String, String>): String? {
    return urls[urls.keys.sortedBy { sizeMap[it] }[0]]
}

/**
 * 检查联系对象是否能执行命令
 *
 * @param subject 联系对象
 * @param user 命令发起者
 * @return 检查结果
 * @see CommandSender.subject
 * @see CommandSender.user
 */
private fun isPermitted(subject: Contact?, user: User?): Boolean {
    return when (PluginConfig.mode) {
        PluginConfig.Mode.Whitelist -> {
            when {
                subject == null -> true
                subject is User && PluginData.userSet.contains(subject.id) -> true
                subject is Group &&
                    PluginData.groupSet.contains(subject.id) &&
                    PluginData.userSet.contains(user?.id) -> true
                else -> false
            }
        }
        PluginConfig.Mode.Blacklist -> {
            when {
                subject == null -> true
                subject is User && !PluginData.userSet.contains(subject.id) -> true
                subject is Group &&
                    !PluginData.groupSet.contains(subject.id) &&
                    !PluginData.userSet.contains(user?.id) -> true
                else -> false
            }
        }
        else -> true
    }
}

/**
 * 检查指令发送者是否能执行指令
 *
 * @param sender
 * @return
 * @see CommandSender
 */
suspend fun checkPermissionAndCooldown(sender: CommandSender): Boolean {
    val subject = sender.subject
    val user = sender.user
    if (!isPermitted(subject, user)) {
        val where = if (subject is Group) "@${subject.id}" else ""
        logger.info("当前模式为'${PluginConfig.mode}'，${user?.id}${where}的命令已被无视")
        return false
    }
    if (getCooldownStatus(subject)) {
        sender.sendMessage(ReplyConfig.inCooldown)
        return false
    }
    return true
}

/**
 * 处理HTTP请求
 *
 * @param sender
 * @param body
 * @return
 */
@OptIn(ExperimentalSerializationApi::class)
suspend fun processRequest(sender: CommandSender, body: RequestBody): ResponseBody? {
    val response: ResponseBody?
    try {
        response = getAPIResponse(body)
    } catch (e: Exception) {
        logger.error(e)
        return null
    }
    logger.info(response.toString())
    if (response.error.isNotEmpty()) {
        sender.sendMessage(ReplyConfig.invokeException)
        logger.warning(response.error)
        return null
    }
    if (response.data.isEmpty()) {
        sender.sendMessage(ReplyConfig.emptyImageData)
        return null
    }
    return response
}

enum class RecallType {
    IMAGE,
    IMAGE_INFO
}

/**
 * 撤回图片或图片信息
 *
 * @param type
 * @param receipt
 * @param time
 */
@OptIn(DelicateCoroutinesApi::class)
suspend fun recall(type: RecallType, receipt: MessageReceipt<Contact>, time: Int) = GlobalScope.launch {
    val result = receipt.recallIn((time * 1000).toLong()).awaitIsSuccess()
    if (result) logger.info("${receipt.target}${type}已撤回")
    else logger.warning("${receipt.target}${type}撤回失败")
}

/**
 * 根据配置生成图片消息
 *
 * @param contact
 * @param imageInfo
 * @param image
 * @return
 */
fun buildMessage(contact: Contact, imageInfo: String, image: Image): SingleMessage {
    return when (PluginConfig.messageType) {
        Simple -> image
        Flash -> FlashImage(image)
        Forward -> buildForwardMessage(contact, CustomDisplayStrategy) {
            add(contact.bot, PlainText(imageInfo))
            add(contact.bot, image)
        }
    }
}

fun Long.toTimeoutMillis(): Long? {
    return if (this < 0L) null
    else if (this == 0L) HttpTimeout.INFINITE_TIMEOUT_MS
    else this
}
