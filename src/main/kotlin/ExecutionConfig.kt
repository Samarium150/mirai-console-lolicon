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

import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import org.jetbrains.annotations.Nullable

/**
 * The configuration used during get-command
 * <br>
 * 执行get命令时的配置
 *
 * @property apikey [PluginConfig.apikey]
 * @property r18 [PluginData.customR18Users] and [PluginData.customR18Groups]
 * @property recall [PluginConfig.recall]
 * @property cooldown [PluginConfig.cooldown]
 * @constructor Create a configuration instance <br> 实例化配置
 * @see PluginConfig
 * @see PluginData
 */
data class ExecutionConfig(
    var apikey: String = PluginConfig.apikey,
    var r18: Int = 0,
    var recall: Int = PluginConfig.recall,
    var cooldown: Int = PluginConfig.cooldown
) {
    /**
     * Set [apikey] to [value]
     * <br>
     * 将 [apikey] 的值设置为 [value]
     *
     * @param value Input value <br> 输入的值
     */
    private fun setAPIKey(@Nullable value: String?) {
        if (value != null) apikey = value
    }

    /**
     * Set [r18] to [value]
     * <br>
     * 将 [r18] 的值设置为 [value]
     *
     * @param value Input value <br> 输入的值
     */
    private fun setR18(@Nullable value: Int?) {
        if (value != null) r18 = value
    }

    /**
     * Set [recall] to [value]
     * <br>
     * 将 [recall] 的值设置为 [value]
     *
     * @param value Input value <br> 输入的值
     */
    private fun setRecall(@Nullable value: Int?) {
        if (value != null) this.recall = value
    }

    /**
     * Set [cooldown] to [value]
     * <br>
     * 将 [cooldown] 设置为 [value]
     *
     * @param value Input value <br> 输入的值
     */
    private fun setCooldown(@Nullable value: Int?) {
        if (value != null) this.cooldown = value
    }

    companion object {
        /**
         * Create the instance according to [subject]
         * <br>
         * 根据 [subject] 创建一个 [ExecutionConfig] 实例
         *
         * @param subject [net.mamoe.mirai.console.command.CommandSender.subject]
         * @return Configuration instance <br> 实例化的配置
         * @see Lolicon.get
         */
        @JvmStatic
        fun create(@Nullable subject: Contact?): ExecutionConfig {
            val config = ExecutionConfig()
            when (subject) {
                is User -> {
                    config.setAPIKey(PluginData.customAPIKeyUsers[subject.id])
                    config.setR18(PluginData.customR18Users[subject.id])
                    config.setRecall(PluginData.customRecallUsers[subject.id])
                    config.setCooldown(PluginData.customCooldownUsers[subject.id])
                }
                is Group -> {
                    config.setAPIKey(PluginData.customAPIKeyGroups[subject.id])
                    config.setR18(PluginData.customR18Groups[subject.id])
                    config.setRecall(PluginData.customRecallGroups[subject.id])
                    config.setCooldown(PluginData.customCooldownGroups[subject.id])
                }
            }
            return config
        }
    }
}
