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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * API 返回的图片信息
 *
 * @property pid 图片PID
 * @property p 图片所在页数
 * @property uid 作者UID
 * @property title 图片标题
 * @property author 作者名字
 * @property r18 是否为R18
 * @property width 宽度
 * @property height 高度
 * @property tags 标签
 * @property urls 链接
 * @constructor 实例化图片数据, 参见: [API](https://api.lolicon.app/#/setu?id=%e5%93%8d%e5%ba%94)
 * @see ResponseBody
 */
@ExperimentalSerializationApi
@Serializable
data class ImageData(
    val pid: Int,
    val p: Int,
    val uid: Int,
    val title: String,
    val author: String,
    val r18: Boolean,
    val width: Int,
    val height: Int,
    val tags: List<String>,
    val ext: String,
    val uploadDate: Long,
    val urls: Map<String, String>
) {
    override fun toString(): String {
        return "ImageData" + Json.encodeToString(this)
    }

    /**
     * 返回具有可读性的信息
     *
     * @return 具有可读性的信息
     */
    fun toReadable(): String {
        return (
            "标题: ${title}\n" +
                "作者: $author (uid: ${uid})\n" +
                "标签: ${tags}\n" +
                "链接: https://pixiv.net/artworks/${pid}"
            )
    }
}
