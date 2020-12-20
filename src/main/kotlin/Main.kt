package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.utils.info

object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "org.example.mirai-plugin",
        version = "0.1.0"
    )
) {
    override fun onEnable() {
        logger.info { "Plugin loaded" }
    }
}