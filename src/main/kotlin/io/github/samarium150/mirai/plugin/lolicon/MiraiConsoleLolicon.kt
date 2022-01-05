/**
 * Copyright (c) 2020-2021 Samarium
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
import io.github.samarium150.mirai.plugin.lolicon.config.CommandConfig
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.config.ProxyConfig
import io.github.samarium150.mirai.plugin.lolicon.config.ReplyConfig
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import io.github.samarium150.mirai.plugin.lolicon.util.getProxyType
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.util.*
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.cancel
import net.mamoe.mirai.console.permission.PermissionService.Companion.permit
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * 插件主类
 *
 * @constructor 创建插件实例
 * @see KotlinPlugin
 */
object MiraiConsoleLolicon : KotlinPlugin(
    JvmPluginDescription(
        id = "io.github.samarium150.mirai.plugin.mirai-console-lolicon",
        version = "5.0.0-beta.7",
        name = "Lolicon"
    ) {
        author("Samarium150")
        info("基于LoliconAPI的涩图插件")
    }
) {

    /**
     * Ktor HTTP客户端
     */
    lateinit var client: HttpClient

    /**
     * 插件启用时调用
     */
    @OptIn(KtorExperimentalAPI::class)
    override fun onEnable() {

        // 重载配置和数据
        PluginConfig.reload()
        CommandConfig.reload()
        ReplyConfig.reload()
        ProxyConfig.reload()
        PluginData.reload()

        if (PluginConfig.master != 0L) {
            PluginData.trustedUsers.add(PluginConfig.master)
            if (PluginConfig.mode == "whitelist")
                PluginData.userSet.add(PluginConfig.master)
            if (PluginConfig.mode == "blacklist")
                PluginData.userSet.remove(PluginConfig.master)
        } else logger.warning("请先在配置文件设置Bot所有者id")

        client = HttpClient {
            engine {
                proxy = if (ProxyConfig.type != "DIRECT") Proxy(
                    getProxyType(ProxyConfig.type),
                    InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
                ) else Proxy.NO_PROXY
            }
            install(JsonFeature) {
                serializer = KotlinxSerializer(kotlinx.serialization.json.Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }

        // 注册命令
        Lolicon.register()

        // 授予权限
        try {
            AbstractPermitteeId.AnyContact.permit(Lolicon.permission)
        } catch (e: Exception) {
            logger.warning("无法自动授予权限，请自行使用权限管理来授予权限")
        }

        logger.info("Plugin loaded")
    }

    /**
     * 插件禁用时调用
     */
    override fun onDisable() {

        // 撤销权限
        try {
            AbstractPermitteeId.AnyContact.cancel(Lolicon.permission, true)
        } catch (e: Exception) {
            logger.warning("无法自动撤销权限，请自行使用权限管理来撤销权限")
        }

        // 注销命令
        Lolicon.unregister()

        client.close()

        logger.info("Plugin unloaded")
    }
}
