/*
 * Copyright (c) 2020-2023 Samarium
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

import io.github.samarium150.mirai.plugin.lolicon.data.PluginData
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.contact.User

internal data class ExecutionConfig(
    val r18: Int = 0,
    val recall: Int = PluginConfig.recall,
    val cooldown: Int = PluginConfig.cooldown
) {
    companion object {
        @JvmStatic
        operator fun invoke(subject: Contact?): ExecutionConfig {
            return when (subject) {
                is User -> {
                    ExecutionConfig(
                        PluginData.customR18Users[subject.id] ?: 0,
                        PluginData.customRecallUsers[subject.id] ?: PluginConfig.recall,
                        PluginData.customCooldownUsers[subject.id] ?: PluginConfig.cooldown
                    )
                }

                is Group -> {
                    ExecutionConfig(
                        PluginData.customR18Groups[subject.id] ?: 0,
                        PluginData.customRecallGroups[subject.id] ?: PluginConfig.recall,
                        PluginData.customCooldownGroups[subject.id] ?: PluginConfig.cooldown
                    )
                }

                else -> ExecutionConfig()
            }
        }
    }
}
