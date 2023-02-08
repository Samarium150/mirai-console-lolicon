/*
 * Copyright (c) 2023 Samarium
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
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
