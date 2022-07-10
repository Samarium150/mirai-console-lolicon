package io.github.samarium150.mirai.plugin.lolicon.config

import io.ktor.client.plugins.*
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object TimeoutConfig : AutoSavePluginConfig("TimeoutConfig") {

    @ValueDescription("整个HTTP请求的超时时间，单位：毫秒")
    val requestTimeoutMillis: Long by value(-1L)

    @ValueDescription("建立连接的超时时间，单位：毫秒")
    val connectTimeoutMillis: Long by value(-1L)

    @ValueDescription("两个数据包之间间隔的超时时间，单位：毫秒")
    val socketTimeoutMillis: Long by value(-1L)

    private fun Long.toTimeoutMillis(): Long? {
        return if (this < 0L) null
        else if (this == 0L) HttpTimeout.INFINITE_TIMEOUT_MS
        else this
    }

    operator fun invoke(): Triple<Long?, Long?, Long?> {
        return Triple(
            requestTimeoutMillis.toTimeoutMillis(),
            connectTimeoutMillis.toTimeoutMillis(),
            socketTimeoutMillis.toTimeoutMillis()
        )
    }
}
