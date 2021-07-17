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

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The information for making a GET request
 * <br>
 * 进行GET请求所需的信息
 *
 * @property r18
 * @property num
 * @property uid
 * @property keyword
 * @property tag
 * @property size
 * @property proxy
 * @property dataAfter
 * @property dataBefore
 * @property dsc
 * @constructor Create a RequestBody instance <br> 实例化请求参数
 */
@Serializable
data class RequestBody(
    val r18: Int = 0,
    val num: Int = 1,
    val uid: List<Int>? = null,
    val keyword: String? = "",
    val tag: List<List<String>>? = null,
    val size: List<String>? = null,
    val proxy: String = "i.pixiv.cat",
    val dataAfter: Long? = null,
    val dataBefore: Long? = null,
    val dsc: Boolean? = null
) {
    override fun toString(): String {
        val format = Json { encodeDefaults = true }
        return format.encodeToString(value = this)
    }
}
