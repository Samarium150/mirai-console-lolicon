package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object Config: AutoSavePluginConfig("Lolicon") {

    @ValueDescription("是否启用插件")
    var enabled: Boolean by value(true)

    @ValueDescription("默认API key")
    var key: String by value("")
}