package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Reply config
 * <br>
 * 自定义回复语句
 *
 * @constructor Create a Reply config instance <br> 实例化回复配置
 * @see net.mamoe.mirai.console.data.AutoSavePluginConfig
 */
object ReplyConfig: AutoSavePluginConfig("ReplyConfig") {

    /**
     * API returns errors
     * <br>
     * API 返回了错误
     */
    @ValueDescription("API返回了错误")
    val invokeException: String by value("API调用错误，请检查日志")

    /**
     * API returns empty list
     * <br>
     * API 返回了空列表
     */
    @ValueDescription("没有找到相关图片")
    val emptyImageData: String by value("没有找到相关图片, 换个关键词试试吧")

    /**
     * In cooldown
     * <br>
     * 指令未冷却
     */
    @ValueDescription("指令未冷却")
    val inCooldown: String by value("你怎么冲得到处都是")

    /**
     * Network error
     * <br>
     * 获取图片失败
     */
    @ValueDescription("获取图片失败")
    val networkError: String by value("网络连接失败/超时或图片已被删除，之后再试试吧")

    /**
     * Invalid json
     * <br>
     * JSON格式错误
     */
    @ValueDescription("JSON格式错误")
    val invalidJson: String by value("JSON格式错误")

    /**
     * Non admin permission denied
     * <br>
     * 没有群主和管理员权限
     */
    @ValueDescription("没有群主和管理员权限")
    val nonAdminPermissionDenied: String by value("该命令仅限群主和管理员使用")

    /**
     * Untrusted
     * <br>
     * 非受信任的用户
     */
    @ValueDescription("非受信任的用户")
    val untrusted: String by value("非受信任的用户不能设置该属性, 让Bot所有者添加到受信任用户名单后才能使用")

    /**
     * Set succeeded
     * <br>
     * set命令执行成功
     */
    @ValueDescription("set命令执行成功")
    val setSucceeded: String by value("设置成功")

    /**
     * Illegal property
     * <br>
     * 非法属性
     */
    @ValueDescription("非法属性")
    val illegalProperty: String by value("不是有效的属性")

    /**
     * Illegal value
     * <br>
     * 非法值
     */
    @ValueDescription("非法值")
    val illegalValue: String by value("不是有效的数字")

    /**
     * Non master permission denied
     * <br>
     * 不是Bot master
     */
    @ValueDescription("不是Bot master")
    val nonMasterPermissionDenied: String by value("该命令仅能由Bot所有者使用")

    /**
     * No master id
     * <br>
     * 没有填写master qq号
     */
    @ValueDescription("没有填写master qq号")
    val noMasterID: String by value("请先在配置文件设置Bot所有者id")

    /**
     * Trust succeeded
     * <br>
     * 添加到信任列表成功
     */
    @ValueDescription("添加到信任列表成功")
    val trustSucceeded: String by value("添加成功")

    /**
     * Already exists
     * <br>
     * 用户已在信任列表中
     */
    @ValueDescription("用户已在信任列表中")
    val alreadyExists: String by value("该用户已经在名单中")

    /**
     * Distrust succeeded
     * <br>
     * 从信任列表中移除成功
     */
    @ValueDescription("从信任列表中移除成功")
    val distrustSucceeded: String by value("移除成功")

    /**
     * Does not exists
     * <br>
     * 用户不在信任列表中
     */
    @ValueDescription("用户不在信任列表中")
    val doesNotExists: String by value("该用户不在名单中")

    /**
     * Reloaded
     * <br>
     * 配置已重载
     */
    @ValueDescription("配置已重载")
    val reloaded: String by value("配置已重载")
}
