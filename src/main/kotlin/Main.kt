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

import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.cancel
import net.mamoe.mirai.console.permission.PermissionService.Companion.permit
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.utils.info

/**
 * Plugin instance
 */
object Main: KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-lolicon",
        version = "2.2",
        name = "mirai-console-lolicon"
    )
) {

    /**
     * Will be invoked when the plugin is enabled
     */
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        /**
         * Load configurations and data
         */
        PluginConfig.reload()
        PluginData.reload()
        if (PluginConfig.master != 0L)
            PluginData.trustedUsers.add(PluginConfig.master)
        else
            logger.warning("请先在配置文件设置Bot所有者id")

        /**
         * Register commands
         */
        Lolicon.register()

        /**
         * Grant permissions
         */
        AbstractPermitteeId.AnyContact.permit(Lolicon.permission)

        logger.info { "Plugin mirai-console-lolicon loaded" }
    }

    /**
     * Will be invoked when the plugin is disabled
     */
    override fun onDisable() {
        AbstractPermitteeId.AnyContact.cancel(Lolicon.permission, true)
        Lolicon.unregister()
        logger.info { "Plugin mirai-console-lolicon unloaded" }
    }
}
