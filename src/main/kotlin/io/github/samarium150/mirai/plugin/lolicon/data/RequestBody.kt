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
package io.github.samarium150.mirai.plugin.lolicon.data

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * 进行POST请求所需的参数
 *
 * @property r18 是否为R18内容
 * @property num 数量
 * @property uid 作者ID
 * @property keyword 关键词
 * @property tag 标签
 * @property size 图片大小
 * @property proxy 代理地址
 * @property dataAfter 这个时间戳及以后
 * @property dataBefore 这个时间戳及以前
 * @property dsc 是否禁用缩写
 * @constructor 实例化请求参数, 参见: [LoliconAPI](https://api.lolicon.app/#/setu?id=%e8%af%b7%e6%b1%82)
 */
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
    @OptIn(ExperimentalSerializationApi::class)
    override fun toString(): String {
        val format = Json { encodeDefaults = true }
        return format.encodeToString(value = this)
    }
}
