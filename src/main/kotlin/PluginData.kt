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
 */
object PluginData: AutoSavePluginData("Data") {

    /**
     * Groups that enabled R18 option
     */
    @ValueDescription("自定义了R18属性的群")
    var customR18Groups: MutableMap<Long, Int> by value()

    /**
     * Users' id and their apikey mappings
     */
    @ValueDescription("自定义了apikey的用户和对应的apikey")
    var customAPIKeyUsers: MutableMap<Long, String> by value()

    /**
     * Groups' id and their apikey mappings
     */
    @ValueDescription("自定义了apikey的群和对应的apikey")
    var customAPIKeyGroups: MutableMap<Long, String> by value()

    /**
     * Users' id and their cooldown mappings
     */
    @ValueDescription("自定义了冷却时间的用户和对应的值")
    var customCooldownUsers: MutableMap<Long, Int> by value()

    /**
     * Groups' id and their cooldown mappings
     */
    @ValueDescription("自定义了冷却时间的群和对应的值")
    var customCooldownGroups: MutableMap<Long, Int> by value()
}