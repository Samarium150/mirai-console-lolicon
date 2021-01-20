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

import kotlinx.coroutines.CoroutineExceptionHandler
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.command.CommandExecuteResult
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.register
import net.mamoe.mirai.console.command.CommandManager.INSTANCE.unregister
import net.mamoe.mirai.console.command.CommandSender.Companion.toCommandSender
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.permission.AbstractPermitteeId
import net.mamoe.mirai.console.permission.PermissionService.Companion.permit
import net.mamoe.mirai.console.plugin.jvm.JvmPlugin
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.event.ConcurrencyKind
import net.mamoe.mirai.event.EventPriority
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info

/**
 * Plugin instance
 */
object Main: KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-lolicon",
        version = "2.0",
        name = "mirai-console-lolicon"
    )
) {
    /**
     * The listener for listening message events
     * Contacts send commands as messages
     */
    private lateinit var commandListener: Listener<MessageEvent>

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
         * Subscribe events
         */
        commandListener =
            globalEventChannel().subscribeAlways(
                MessageEvent::class,
                CoroutineExceptionHandler { _, throwable -> logger.error(throwable) },
                ConcurrencyKind.CONCURRENT,
                EventPriority.NORMAL
            ) call@ {
                if (!PluginConfig.enabled) return@call
                val sender = this.toCommandSender()
                when (val result = CommandManager.executeCommand(sender, message)) {
                    is CommandExecuteResult.IllegalArgument -> {
                        result.exception.message?.let { sender.sendMessage(it) }
                        // intercept()
                    }
                    is CommandExecuteResult.ExecutionFailed -> {
                        val owner = result.command.owner
                        val (logger, printOwner) = when (owner) {
                            is JvmPlugin -> owner.logger to false
                            else -> MiraiConsole.mainLogger to true
                        }
                        logger.warning(
                            "Exception in executing command `$message`" +
                                if (printOwner) ", command owned by $owner" else "",
                            result.exception
                        )
                        // intercept()
                    }
                }
            }

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
        Lolicon.unregister()
        logger.info { "Plugin mirai-console-lolicon unloaded" }
    }
}
