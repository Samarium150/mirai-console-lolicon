package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

/**
 * Object for auto saving plugin related data
 */
object PluginData: AutoSavePluginData("Data") {

    /**
     * Groups that enabled R18 option
     */
    var r18Groups: MutableList<Long> by value(mutableListOf())

    /**
     * Users' id and their apikey mappings
     */
    var customAPIKeyUsers: MutableMap<Long, String> by value()

    /**
     * Groups' id and their apikey mappings
     */
    var customAPIKeyGroups: MutableMap<Long, String> by value()
}