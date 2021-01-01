package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Object for auto saving plugin configuration
 */
object Config: AutoSavePluginConfig("Config") {

    /**
     * Whether the plugin should be enabled
     */
    @ValueDescription("是否启用插件")
    var enabled: Boolean by value(true)

    /**
     * Default apikey
     */
    @ValueDescription("默认的apikey")
    var apikey: String by value("")
}