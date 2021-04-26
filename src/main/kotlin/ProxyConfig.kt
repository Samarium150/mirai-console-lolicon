package com.github.samarium150.mirai.plugin

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Proxy config
 * <br>
 * 自定义代理
 *
 * @constructor Create empty Proxy config
 * @see net.mamoe.mirai.console.data.AutoSavePluginConfig
 */
object ProxyConfig: AutoSavePluginConfig("ProxyConfig") {

    /**
     * Type
     * <br>
     * 代理类型
     */
    @ValueDescription("可选：DIRECT/HTTP/SOCKS")
    val type: String by value("DIRECT")

    /**
     * Hostname
     * <br>
     * IP地址
     */
    @ValueDescription("地址")
    val hostname: String by value("localhost")

    /**
     * Port
     * <br>
     * 端口
     */
    @ValueDescription("端口")
    val port: Int by value(1080)
}
