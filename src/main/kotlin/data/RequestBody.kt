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
package io.github.samarium150.mirai.plugin.lolicon.data

import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class RequestBody(
    val r18: Int = 0,
    val num: Int = 1,
    val uid: List<Int>? = null,
    val keyword: String? = "",
    val tag: List<List<String>>? = null,
    val size: List<String>? = null,
    val proxy: String = "i.pixiv.re",
    val dataAfter: Long? = null,
    val dataBefore: Long? = null,
    val dsc: Boolean? = null
) {
    override fun toString(): String {
        val format = Json { encodeDefaults = true }
        return format.encodeToString(value = this)
    }
}
