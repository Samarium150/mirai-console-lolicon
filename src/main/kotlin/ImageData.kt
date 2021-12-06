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

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * The image information returned by Lolicon API
 * <br>
 *  Lolicon API 返回的图片信息
 *
 * @property pid Image PID <br> 图片PID
 * @property p the page of the image <br> 图片所在页数
 * @property uid Author UID <br> 作者UID
 * @property title Image title <br> 图片标题
 * @property author Author's name <br> 作者名字
 * @property r18 R18 category <br> 是否为R18
 * @property width Width of the image <br> 宽度
 * @property height Height of the image <br> 高度
 * @property tags Image tags <br> 标签
 * @property urls Image urls <br> 链接
 * @constructor Create an Image data instance <br> 实例化图片数据
 * @see ResponseBody
 */
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

    @OptIn(ExperimentalSerializationApi::class)
    override fun toString(): String {
        return "ImageData" + Json.encodeToString(this)
    }

    /**
     * Return the readable information
     * <br>
     * 返回具有可读性的信息
     *
     * @return readable information <br> 具有可读性的信息
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
