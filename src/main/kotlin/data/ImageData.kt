/*
 * Copyright (c) 2020-2023 Samarium
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

import io.github.samarium150.mirai.plugin.lolicon.config.ReplyConfig
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
    val aiType: Int,
    val uploadDate: Long,
    val urls: Map<String, String>
) {

    override fun toString(): String {
        return "ImageData" + Json.encodeToString(this)
    }

    fun toReadable(url: String? = null): String {
        return StringBuilder().append(
            ReplyConfig.imageDataMessageTemplate
                .replace("{Title}", title, ignoreCase = true)
                .replace("{Author}", author, ignoreCase = true)
                .replace("{UID}", uid.toString(), ignoreCase = true)
                .replace("{Tags}", tags.toString(), ignoreCase = true)
                .replace("{PID}", pid.toString(), ignoreCase = true)
                .replace("{PixivURL}", "https://pixiv.net/artworks/${pid}", ignoreCase = true)
                .replace("{ProxyUrls}", url ?: urls.toString(), ignoreCase = true)
        ).toString()
    }
}
