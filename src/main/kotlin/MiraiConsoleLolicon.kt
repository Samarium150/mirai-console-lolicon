/**
 * Copyright (c) 2020-2022 Samarium
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
package io.github.samarium150.mirai.plugin.lolicon

import io.github.samarium150.mirai.plugin.lolicon.command.Lolicon
import io.github.samarium150.mirai.plugin.lolicon.config.*
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import java.net.InetSocketAddress
import java.net.Proxy

object MiraiConsoleLolicon : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.samarium150.mirai.plugin.mirai-console-lolicon",
        version = "6.0.1",
        name = "Lolicon"
    ) {
        author("Samarium150")
        info("基于LoliconAPI的涩图插件")
    }
) {

    lateinit var client: HttpClient

    override fun onEnable() {

        PluginData.reload()
        PluginConfig.reload()
        ProxyConfig.reload()
        TimeoutConfig.reload()
        ReplyConfig.reload()
        CommandConfig.reload()

        client = HttpClient(OkHttp) {
            engine {
                proxy = if (ProxyConfig.type != Proxy.Type.DIRECT) Proxy(
                    ProxyConfig.type,
                    InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
                ) else Proxy.NO_PROXY
            }
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                TimeoutConfig().apply {
                    requestTimeoutMillis = first
                    connectTimeoutMillis = second
                    socketTimeoutMillis = third
                }
            }
        }

        Lolicon.trusted
        Lolicon.register()

        logger.info("Plugin loaded")
    }

    override fun onDisable() {

        Lolicon.unregister()

        client.close()

        logger.info("Plugin unloaded")
    }
}
