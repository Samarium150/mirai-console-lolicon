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

    private val template = """
        标题: $title
        作者: $author (uid: ${uid})
        标签: $tags
        链接: https://pixiv.net/artworks/${pid}
        代理链接：
    """.trimIndent()

    override fun toString(): String {
        return "ImageData" + Json.encodeToString(this)
    }

    fun toReadable(url: String): String {
        return template + url
    }

    fun toReadable(urls: Map<String, String>): String {
        return template + urls.toString()
    }
}
