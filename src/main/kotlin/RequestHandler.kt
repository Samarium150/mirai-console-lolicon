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

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*

/**
 * Object for handling GET request
 */
object RequestHandler {

    /**
     * The Gson instance
     */
    private val gson = Gson()

    /**
     * Makes a GET request to Lolicon API
     * with the given [parameters]
     *
     * @param parameters [RequestParams]
     * @return [Response]
     * @throws FuelError if GET request is failed
     * @throws JsonSyntaxException if returned JSON is invalid
     * @throws APIException if Lolicon API didn't return status 0
     */
    @Throws(FuelError::class, JsonSyntaxException::class, APIException::class)
    fun get(parameters: RequestParams): Response {
        val url = "https://api.lolicon.app/setu/?$parameters"
        val (_, response, result) = Fuel.get(url).responseString()
        if (result is Result.Failure) throw result.getException()
        val feedback: Response = gson.fromJson(String(response.data), Response::class.java)
        if (feedback.code != 0) throw APIException(feedback.code, feedback.msg)
        return feedback
    }

    /**
     * Downloads the image from [url]
     *
     * @param url [String] URL from [ImageData.url]
     * @return [InputStream] The image [ByteArrayInputStream]
     * @throws FuelError if download failed
     */
    @Throws(FuelError::class)
    fun download(url: String): InputStream {
        if (PluginConfig.save) {
            var file: File? = null
            val (_, _, result) = Fuel
                .download(url)
                .fileDestination { _, _ ->
                    val urlPaths = url.split("/")
                    val dir = File(System.getProperty("user.dir") + "/data/mirai-console-lolicon/download/")
                    if (!dir.exists()) dir.mkdirs()
                    file = File("${dir}/${urlPaths[urlPaths.lastIndex]}")
                    file!!
                }.responseString()
            if (result is Result.Failure) throw result.getException()
            return ByteArrayInputStream(file!!.readBytes())
        }
        var outputStream = OutputStream.nullOutputStream()
        val (_, _, result) = Fuel
            .download(url)
            .streamDestination { response: com.github.kittinunf.fuel.core.Response, _ ->
                outputStream = ByteArrayOutputStream(response.contentLength.toInt())
                Pair(outputStream as ByteArrayOutputStream) {
                    InputStream.nullInputStream()
                }
            }.responseString()
        if (result is Result.Failure) throw result.getException()
        return ByteArrayInputStream((outputStream as ByteArrayOutputStream).toByteArray())
    }
}
