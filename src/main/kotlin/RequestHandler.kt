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

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream

/**
 * Object for handling GET request
 *
 * @constructor Create a handler instance <br> 实例化处理程序
 */
object RequestHandler {

    suspend fun get(body: RequestBody): ResponseBody {
        return Main.client.post("https://api.lolicon.app/setu/v2") {
            contentType(ContentType.Application.Json)
            this.body = body
        }
    }

    /**
     * Downloads the image from [url]
     * <br>
     * 从 [url] 下载图片
     *
     * @param url URL from [ImageData.urls] <br> 来自 [ImageData.urls] 的 URL
     * @return The image [ByteArrayInputStream] <br> 图片字节输入流
     * @see ImageData
     */
    suspend fun download(url: String): InputStream {
        val response: HttpResponse = Main.client.get(url)
        val result: ByteArray = response.receive()
        if (PluginConfig.save) {
            val dir = File(System.getProperty("user.dir") + "/data/mirai-console-lolicon/download/")
            if (!dir.exists()) dir.mkdirs()
            val urlPaths = url.split("/")
            val file = File("${dir}/${urlPaths[urlPaths.lastIndex]}")
            file.writeBytes(result)
        }
        return ByteArrayInputStream(result)
    }
}
