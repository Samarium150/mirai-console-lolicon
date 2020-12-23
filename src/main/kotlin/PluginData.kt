package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

object PluginData: AutoSavePluginData("Data") {
    var r18Groups: MutableList<Long> by value(mutableListOf())
    var customAPIKeyUsers: MutableMap<Long, String> by value()
    var customAPIKeyGroups: MutableMap<Long, String> by value()
}