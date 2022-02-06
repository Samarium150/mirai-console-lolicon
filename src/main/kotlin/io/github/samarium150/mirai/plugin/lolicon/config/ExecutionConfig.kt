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
package io.github.samarium150.mirai.plugin.lolicon.config

import io.github.samarium150.mirai.plugin.lolicon.command.Lolicon
import io.github.samarium150.mirai.plugin.lolicon.command.Lolicon.get
import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User
import org.jetbrains.annotations.Nullable

/**
 * 执行get命令时的配置
 *
 * @property r18 是否为R18模式
 * @property recall 撤回时间
 * @property cooldown 冷却时间
 * @constructor 实例化配置
 */
internal data class ExecutionConfig(
    var r18: Int = 0,
    var recall: Int = PluginConfig.recall,
    var cooldown: Int = PluginConfig.cooldown
) {

    /**
     * 将 [r18] 的值设置为 [value]
     *
     * @param value 输入的值
     */
    private fun setR18(@Nullable value: Int?) {
        if (value != null) r18 = value
    }

    /**
     * 将 [recall] 的值设置为 [value]
     *
     * @param value 输入的值
     */
    private fun setRecall(@Nullable value: Int?) {
        if (value != null) this.recall = value
    }

    /**
     * 将 [cooldown] 设置为 [value]
     *
     * @param value 输入的值
     */
    private fun setCooldown(@Nullable value: Int?) {
        if (value != null) this.cooldown = value
    }

    companion object {
        /**
         * 根据 [subject] 创建一个 [ExecutionConfig] 实例
         *
         * @param subject 联系对象
         * @return 实例化的配置
         * @see Lolicon.get
         * @see CommandSender.subject
         */
        @JvmStatic
        fun create(@Nullable subject: Contact?): ExecutionConfig {
            val config = ExecutionConfig()
            when (subject) {
                is User -> {
                    config.setR18(PluginData.customR18Users[subject.id])
                    config.setRecall(PluginData.customRecallUsers[subject.id])
                    config.setCooldown(PluginData.customCooldownUsers[subject.id])
                }
                is Group -> {
                    config.setR18(PluginData.customR18Groups[subject.id])
                    config.setRecall(PluginData.customRecallGroups[subject.id])
                    config.setCooldown(PluginData.customCooldownGroups[subject.id])
                }
            }
            return config
        }
    }
}
