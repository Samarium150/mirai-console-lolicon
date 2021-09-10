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

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Object for auto saving plugin related data
 * <br>
 * 插件数据
 *
 * @constructor Create a plugin config instance <br> 实例化插件数据
 * @see net.mamoe.mirai.console.data.AutoSavePluginData
 */
object PluginData: AutoSavePluginData("Data") {

    /**
     * User set
     * <br>
     * 用户列表
     */
    @ValueDescription("用户列表")
    val userSet: MutableSet<Long> by value()

    /**
     * Group set
     * <br>
     * 群组列表
     */
    @ValueDescription("群组列表")
    val groupSet: MutableSet<Long> by value()

    /**
     * Trusted Users
     * <br>
     * 受信任的用户
     */
    @ValueDescription("受信任的用户")
    val trustedUsers: MutableSet<Long> by value()

    /**
     * Users that enabled R18 option
     * <br>
     * 自定义了 R18 属性的用户
     */
    @ValueDescription("自定义了R18属性的用户")
    val customR18Users: MutableMap<Long, Int> by value()

    /**
     * Groups that enabled R18 option
     * <br>
     * 自定义了 R18 属性的群
     */
    @ValueDescription("自定义了R18属性的群")
    val customR18Groups: MutableMap<Long, Int> by value()

    /**
     * Users' id and their recall time mappings
     * <br>
     * 自定义了撤回时间的用户和对应的值
     */
    @ValueDescription("自定义了撤回时间的用户和对应的值")
    val customRecallUsers: MutableMap<Long, Int> by value()

    /**
     * Groups' id and their recall time mappings
     * <br>
     * 自定义了撤回时间的用户和对应的值
     */
    @ValueDescription("自定义了撤回时间的用户和对应的值")
    val customRecallGroups: MutableMap<Long, Int> by value()

    /**
     * Users' id and their cooldown mappings
     * <br>
     * 自定义了冷却时间的用户和对应的值
     */
    @ValueDescription("自定义了冷却时间的用户和对应的值")
    val customCooldownUsers: MutableMap<Long, Int> by value()

    /**
     * Groups' id and their cooldown mappings
     * <br>
     * 自定义了冷却时间的群和对应的值
     */
    @ValueDescription("自定义了冷却时间的群和对应的值")
    val customCooldownGroups: MutableMap<Long, Int> by value()
}
