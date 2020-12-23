package com.github.samarium150.mirai.plugin

import com.github.samarium150.mirai.plugin.Config.enabled
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
import net.mamoe.mirai.event.Listener
import net.mamoe.mirai.event.events.MessageEvent
import net.mamoe.mirai.event.subscribeAlways
import net.mamoe.mirai.utils.info

object Main : KotlinPlugin(
    JvmPluginDescription(
        id = "com.github.samarium150.mirai-console-lolicon",
        version = "0.1.0"
    )
) {
    private lateinit var commandListener: Listener<MessageEvent>
    @OptIn(ConsoleExperimentalApi::class, ExperimentalCommandDescriptors::class)
    override fun onEnable() {
        Config.reload()
        PluginData.reload()
        commandListener = subscribeAlways(
            coroutineContext = CoroutineExceptionHandler { _, throwable ->
                logger.error(throwable)
            },
            concurrency = Listener.ConcurrencyKind.CONCURRENT,
            priority = Listener.EventPriority.NORMAL
        ) call@{
            if (!enabled) return@call
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
        Lolicon.register()
        AbstractPermitteeId.AnyContact.permit(Lolicon.permission)
        logger.info { "Plugin mirai-console-lolicon loaded" }
    }

    override fun onDisable() {
        Lolicon.unregister()
        logger.info { "Plugin mirai-console-lolicon unloaded" }
    }
}