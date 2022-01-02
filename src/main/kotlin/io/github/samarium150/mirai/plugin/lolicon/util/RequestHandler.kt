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
package io.github.samarium150.mirai.plugin.lolicon.util

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.data.ImageData
import io.github.samarium150.mirai.plugin.lolicon.data.RequestBody
import io.github.samarium150.mirai.plugin.lolicon.data.ResponseBody
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * 发送包含 [RequestBody] 的POST请求给 [API](https://api.lolicon.app/setu/v2)
 *
 * @param body 请求参数
 * @return API返回的数据
 * @see RequestBody
 * @see ResponseBody
 */
suspend fun getAPIResponse(body: RequestBody): ResponseBody {
    return MiraiConsoleLolicon.client.post("https://api.lolicon.app/setu/v2") {
        contentType(ContentType.Application.Json)
        this.body = body
    }
}

/**
 * 从 [url] 下载图片, 并返回 [ByteArrayInputStream] 形式的图片数据
 *
 * @param url 来自 [ImageData.urls] 的 URL
 * @return 图片字节输入流
 * @see ImageData
 */
suspend fun downloadImage(url: String): InputStream {
    val response: HttpResponse = MiraiConsoleLolicon.client.get(url)
    val result: ByteArray = response.receive()
    if (PluginConfig.save) {
        val dir = File(System.getProperty("user.dir") + "${cachePath}/")
        if (!dir.exists()) dir.mkdirs()
        val urlPaths = url.split("/")
        val file = File("${dir}/${urlPaths[urlPaths.lastIndex]}")
        file.writeBytes(result)
    }
    return ByteArrayInputStream(result)
}
