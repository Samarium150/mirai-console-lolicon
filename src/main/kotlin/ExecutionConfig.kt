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

/**
 * The configuration used during get-command
 *
 * @property apikey [String]
 * @property r18 [Int]
 * @property recall [Int]
 * @property cooldown [Int]
 * @constructor
 */
data class ExecutionConfig(
    var apikey: String = PluginConfig.apikey,
    var r18: Int = 0,
    var recall: Int = PluginConfig.recall,
    var cooldown: Int = PluginConfig.cooldown
) {
    /**
     * Update [apikey] to [value]
     *
     * @param value [String]?
     */
    private fun setAPIKey(value: String?) {
        if (value != null) apikey = value
    }

    /**
     * Update [r18] to [value]
     *
     * @param value [Int]?
     */
    private fun setR18(value: Int?) {
        if (value != null) r18 = value
    }

    /**
     * Update [recall] to [value]
     *
     * @param value [Int]?
     */
    private fun setRecall(value: Int?) {
        if (value != null) this.recall = value
    }

    /**
     * Update [cooldown] to [value]
     *
     * @param value [Int]?
     */
    private fun setCooldown(value: Int?) {
        if (value != null) this.cooldown = value
    }

    companion object {
        /**
         * Create the instance according to [subject]
         *
         * @param subject [Contact]?
         * @return [ExecutionConfig]
         */
        @JvmStatic
        fun create(subject: Contact?): ExecutionConfig {
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
