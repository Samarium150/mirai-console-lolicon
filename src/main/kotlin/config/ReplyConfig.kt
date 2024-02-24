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
package io.github.samarium150.mirai.plugin.lolicon.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object ReplyConfig : AutoSavePluginConfig("ReplyConfig") {

    @ValueDescription("回复消息模板, 可用占位符\n" +
        "标题 {Title}, 图片PID {Pid}, 图片标签 {Tags}\n" +
        "作者名称 {AuthorName}, 作者UID {AuthorUid}\n" +
        "P站链接 {PixivUrl}, 图片代理链接 {ProxyUrls}")
    val replyMessageTemplate: Array<String> by value(arrayOf(
        "标题: {Title}",
        "作者: {AuthorName} [uid: {AuthorUid}]",
        "标签: {Tags}",
        "链接: {PixivUrl}",
        "代理链接: {ProxyUrls}"
        )
    )

    @ValueDescription("API返回了错误")
    val invokeException: String by value("API调用错误，请检查日志")

    @ValueDescription("没有找到相关图片")
    val emptyImageData: String by value("没有找到相关图片, 换个关键词试试吧")

    @ValueDescription("指令未冷却")
    val inCooldown: String by value("你怎么冲得到处都是")

    @ValueDescription("标签被过滤")
    val filteredTag: String by value("该图片标签已被过滤，请换个标签再试")

    @ValueDescription("获取图片失败")
    val networkError: String by value("网络连接失败/超时或图片已被删除，之后再试试吧")

    @ValueDescription("JSON格式错误")
    val invalidJson: String by value("JSON格式错误")

    @ValueDescription("没有群主和管理员权限")
    val nonAdminPermissionDenied: String by value("该命令仅限群主和管理员使用")

    @ValueDescription("非受信任的用户")
    val untrusted: String by value("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")

    @ValueDescription("set命令执行成功")
    val setSucceeded: String by value("设置成功")

    @ValueDescription("非法值")
    val illegalValue: String by value("不是有效的数字")

    @ValueDescription("提示Get和Adv命令已受理")
    val notify: String by value("正在获取图片...")
}
