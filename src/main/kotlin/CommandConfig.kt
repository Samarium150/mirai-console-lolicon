package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Command config
 * <br>
 * 实验性质的自定义命令
 *
 * @constructor Create a Command config instance <br> 实例化命令配置
 * @see net.mamoe.mirai.console.data.AutoSavePluginConfig
 */
object CommandConfig: AutoSavePluginConfig("CommandConfig") {

    /**
     * Composite command secondary names
     * <br>
     * 复合命令的别名
     */
    @ValueDescription("复合命令的别名")
    val lolicon: Array<String> by value(arrayOf("涩图"))
}
