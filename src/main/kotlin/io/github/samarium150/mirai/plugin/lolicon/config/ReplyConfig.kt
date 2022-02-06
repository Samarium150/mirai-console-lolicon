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
package io.github.samarium150.mirai.plugin.lolicon.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * 回复语句的配置
 *
 * @constructor 实例化回复语句的配置
 * @see AutoSavePluginConfig
 */
object ReplyConfig : AutoSavePluginConfig("ReplyConfig") {

    /**
     * API 返回了错误
     */
    @ValueDescription("API返回了错误")
    val invokeException: String by value("API调用错误，请检查日志")

    /**
     * API 返回了空列表
     */
    @ValueDescription("没有找到相关图片")
    val emptyImageData: String by value("没有找到相关图片, 换个关键词试试吧")

    /**
     * 指令未冷却
     */
    @ValueDescription("指令未冷却")
    val inCooldown: String by value("你怎么冲得到处都是")

    /**
     * 标签被过滤
     */
    @ValueDescription("标签被过滤")
    val filteredTag: String by value("该图片标签已被过滤，请换个标签再试")

    /**
     * 获取图片失败
     */
    @ValueDescription("获取图片失败")
    val networkError: String by value("网络连接失败/超时或图片已被删除，之后再试试吧")

    /**
     * JSON格式错误
     */
    @ValueDescription("JSON格式错误")
    val invalidJson: String by value("JSON格式错误")

    /**
     * 没有群主和管理员权限
     */
    @ValueDescription("没有群主和管理员权限")
    val nonAdminPermissionDenied: String by value("该命令仅限群主和管理员使用")

    /**
     * 非受信任的用户
     */
    @ValueDescription("非受信任的用户")
    val untrusted: String by value("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")

    /**
     * set命令执行成功
     */
    @ValueDescription("set命令执行成功")
    val setSucceeded: String by value("设置成功")

    /**
     * 非法属性
     */
    @ValueDescription("非法属性")
    val illegalProperty: String by value("不是有效的属性")

    /**
     * 非法值
     */
    @ValueDescription("非法值")
    val illegalValue: String by value("不是有效的数字")

    /**
     * 不是Bot master
     */
    @ValueDescription("不是Bot master")
    val nonMasterPermissionDenied: String by value("该命令仅能由Bot所有者使用")

    /**
     * 没有填写master qq号
     */
    @ValueDescription("没有填写master qq号")
    val noMasterID: String by value("请先在配置文件设置Bot所有者id")

    /**
     * 添加到信任列表成功
     */
    @ValueDescription("添加到信任列表成功")
    val trustSucceeded: String by value("添加成功")

    /**
     * 用户已在信任列表中
     */
    @ValueDescription("用户已在信任列表中")
    val alreadyExists: String by value("该用户已经在名单中")

    /**
     * 从信任列表中移除成功
     */
    @ValueDescription("从信任列表中移除成功")
    val distrustSucceeded: String by value("移除成功")

    /**
     * 用户不在信任列表中
     */
    @ValueDescription("用户不在信任列表中")
    val doesNotExists: String by value("该用户不在名单中")

}
