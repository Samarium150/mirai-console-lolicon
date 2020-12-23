package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Object for auto saving plugin related data
 */
object PluginData: AutoSavePluginData("Data") {

    /**
     * Groups that enabled R18 option
     */
    @ValueDescription("启用R18的群")
    var r18Groups: MutableList<Long> by value(mutableListOf())

    /**
     * Users' id and their apikey mappings
     */
    @ValueDescription("自定义了apikey的用户和对应的apikey")
    var customAPIKeyUsers: MutableMap<Long, String> by value()

    /**
     * Groups' id and their apikey mappings
     */
    @ValueDescription("自定义了apikey的群和对应的apikey")
    var customAPIKeyGroups: MutableMap<Long, String> by value()
}