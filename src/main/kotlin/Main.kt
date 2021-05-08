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
package com.github.samarium150.mirai.plugin

import com.github.kittinunf.fuel.core.FuelManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.cancel
import net.mamoe.mirai.console.permission.PermissionService.Companion.permit
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Plugin instance
 * <br>
 * 插件实例
 *
 * @constructor Create a KotlinPlugin instance <br> 实例化插件
 * @see net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
 */
@ConsoleExperimentalApi
object Main: KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-lolicon",
        version = "3.4.0",
        name = "mirai-console-lolicon"
    )
) {

    /**
     * Will be invoked when the plugin is enabled
     * <br>
     * 插件启用时将被调用
     */
    override fun onEnable() {
        /**
         * Load configurations and data
         * <br>
         * 加载配置及数据
         */
        PluginConfig.reload()
        CommandConfig.reload()
        ReplyConfig.reload()
        ProxyConfig.reload()
        PluginData.reload()

        FuelManager.instance.proxy = if (ProxyConfig.type != "DIRECT") Proxy(
            Utils.getProxyType(ProxyConfig.type),
            InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
        ) else Proxy.NO_PROXY

        if (PluginConfig.master != 0L) {
            PluginData.trustedUsers.add(PluginConfig.master)
            PluginData.reload()
        } else logger.warning("请先在配置文件设置Bot所有者id")

        /**
         * Register commands
         * <br>
         * 注册命令
         */
        Lolicon.register()

        /**
         * Grant permissions
         * <br>
         * 授予权限
         */
        AbstractPermitteeId.AnyContact.permit(Lolicon.permission)

        logger.info("Plugin mirai-console-lolicon loaded")
    }

    /**
     * Will be invoked when the plugin is disabled
     * <br>
     * 插件禁用时调用
     */
    override fun onDisable() {
        /**
         * Revoke permissions
         * <br>
         * 撤销权限
         */
        AbstractPermitteeId.AnyContact.cancel(Lolicon.permission, true)

        /**
         * Unregister commands
         * <br>
         * 注销命令
         */
        Lolicon.unregister()

        logger.info("Plugin mirai-console-lolicon unloaded")
    }
}
