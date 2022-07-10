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
package io.github.samarium150.mirai.plugin.lolicon.util

import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.data.RequestBody
import io.github.samarium150.mirai.plugin.lolicon.data.ResponseBody
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import java.io.File
import java.io.InputStream

suspend fun getAPIResponse(body: RequestBody): ResponseBody {
    return MiraiConsoleLolicon.client.post("https://api.lolicon.app/setu/v2") {
        contentType(ContentType.Application.Json)
        setBody(body)
    }.body()
}

suspend fun downloadImage(url: String): InputStream {
    val response: HttpResponse = MiraiConsoleLolicon.client.get(url)
    val result: ByteArray = response.body()
    if (PluginConfig.save) {
        val urlPaths = url.split("/")
        val file = cacheFolder.resolve(urlPaths[urlPaths.lastIndex])
        file.writeBytes(result)
    }
    return result.inputStream()
}

suspend fun getImageInputStream(url: String): InputStream {
    return if (PluginConfig.save && PluginConfig.cache) {
        val paths = url.split("/")
        val path = "$cacheFolder/${paths[paths.lastIndex]}"
        val cache = File(System.getProperty("user.dir") + path)
        if (cache.exists()) cache.inputStream()
        else downloadImage(url)
    } else downloadImage(url)
}
