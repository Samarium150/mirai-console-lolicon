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

import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.User

/**
 * Object for utility functions
 */
object Utils {

    /**
     * Check whether [user] is the bot owner
     *
     * @param user [User]?
     * @return [Boolean]
     */
    fun checkMaster(user: User?): Boolean {
        return user == null || user.id == PluginConfig.master
    }

    /**
     * Check whether [user] is trusted
     *
     * @param user [User]?
     * @return [Boolean]
     */
    fun checkUserPerm(user: User?): Boolean {
        return user == null || PluginData.trustedUsers.contains(user.id)
    }

    /**
     * Check whether [user] is a group owner or administrator
     *
     * @param user [User]?
     * @return [Boolean]
     */
    fun checkMemberPerm(user: User?): Boolean {
        return (user as Member).permission != MemberPermission.MEMBER
    }

    /**
     * Convert [value] into a valid number
     * for setting the corresponding property
     *
     * @param value [String]
     * @param type [String]
     * @return [Int]
     * @throws NumberFormatException if [value] is invalid
     */
    @Throws(NumberFormatException::class)
    fun convertValue(value: String, type: String): Int {
        val setting = value.toInt()
        when (type) {
            "r18" -> if (setting != 0 && setting != 1 && setting != 2) throw NumberFormatException(value)
            "recall" -> if (setting < 0 || setting >= 120) throw NumberFormatException(value)
            "cooldown" -> if (setting < 0) throw NumberFormatException(value)
        }
        return setting
    }
}
