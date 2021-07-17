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

import com.github.samarium150.mirai.plugin.Lolicon.set
import net.mamoe.mirai.contact.Member
import net.mamoe.mirai.contact.MemberPermission
import net.mamoe.mirai.contact.User
import org.jetbrains.annotations.Nullable
import java.net.Proxy

/**
 * Object for utility functions
 * <br>
 * 实用函数
 *
 * @constructor Create a Utils instance <br> 实例化Utils
 */
object Utils {

    /**
     * Check whether [user] is the bot owner
     * <br>
     * 检查用户是否是Bot所有者
     *
     * @param user target user <br> 目标用户
     * @return checking result <br> 检查结果
     */
    fun checkMaster(@Nullable user: User?): Boolean {
        return user == null || user.id == PluginConfig.master
    }

    /**
     * Check whether [user] is trusted
     * <br>
     * 检查用户是否受信任
     *
     * @param user target user <br> 目标用户
     * @return checking result <br> 检查结果
     */
    fun checkUserPerm(@Nullable user: User?): Boolean {
        return user == null || PluginData.trustedUsers.contains(user.id)
    }

    /**
     * Check whether [user] is a group owner or administrator
     * <br>
     * 检查用户在群里的权限
     *
     * @param user target user <br> 目标用户
     * @return checking result <br> 检查结果
     */
    fun checkMemberPerm(@Nullable user: User?): Boolean {
        return (user as Member).permission != MemberPermission.MEMBER
    }

    /**
     * Convert [value] into a valid number
     * for setting the corresponding property
     * <br>
     * 将字符串转为整数值
     *
     * @param value input string value <br> 输入的字符串
     * @param type input property <br> 需要转化的类别
     * @return integer value <br> 转换后的值
     * @throws NumberFormatException if [value] is invalid <br> 数值非法时抛出
     * @see Lolicon.set
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

    /**
     * Get proxy type from the given string
     * <br>
     * 根据输入值返回代理类型
     *
     * @param value input string value <br> 输入的字符串
     * @return [Proxy.Type] <br> 代理的类型
     * @throws IllegalArgumentException if [value] is not [Proxy.Type] <br> 数值非法时抛出
     * @see Proxy
     */
    @Throws(IllegalArgumentException::class)
    fun getProxyType(value: String): Proxy.Type {
        return when (value) {
            "DIRECT" -> Proxy.Type.DIRECT
            "HTTP" -> Proxy.Type.HTTP
            "SOCKS" -> Proxy.Type.SOCKS
            else -> throw IllegalArgumentException(value)
        }
    }

    /**
     * Process tags
     *
     * @param str
     * @return
     */
    fun processTags(str: String): List<List<String>> {
        val result: MutableList<List<String>> = listOf<List<String>>().toMutableList()
        val and = str.split("&")
        for (s in and) result.add(s.split("|"))
        return result.toList()
    }
}
