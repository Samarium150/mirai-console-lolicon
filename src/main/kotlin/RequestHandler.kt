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

import com.github.kittinunf.fuel.core.FuelError
import com.github.kittinunf.fuel.core.FuelManager
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.*
import java.lang.IllegalArgumentException
import java.net.InetSocketAddress
import java.net.Proxy

/**
 * Object for handling GET request
 *
 * @constructor Create a handler instance <br> 实例化处理程序
 */
object RequestHandler {

    /**
     * The Gson instance
     * <br>
     * Gson 实例
     */
    private val gson = Gson()

    /**
     * Makes a GET request to Lolicon API
     * with the given [parameters]
     * <br>
     * 根据参数发送HTTP请求
     *
     * @param parameters [RequestParams]
     * @return [Response]
     * @throws FuelError if GET request is failed <br> GET请求失败时抛出
     * @throws JsonSyntaxException if returned JSON is invalid <br> 返回的JSON无效时抛出
     * @throws APIException if Lolicon API didn't return status 0 <br> API调用错误时抛出
     * @see RequestParams
     * @see Response
     * @see APIException
     */
    @Throws(FuelError::class, JsonSyntaxException::class, APIException::class)
    fun get(parameters: RequestParams): Response {
        val url = "https://api.lolicon.app/setu/?$parameters"
        val (_, response, result) = FuelManager()
            .also {
                try {
                    it.proxy = Proxy(
                        Utils.getProxyType(ProxyConfig.type),
                        InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
                    )
                } catch (iae: IllegalArgumentException) { }
            }
            .get(url)
            .responseString()
        if (result is Result.Failure) throw result.getException()
        val feedback: Response = gson.fromJson(String(response.data), Response::class.java)
        if (feedback.code != 0) throw APIException(feedback.code, feedback.msg)
        return feedback
    }

    /**
     * Downloads the image from [url]
     * <br>
     * 从 [url] 下载图片
     *
     * @param url URL from [ImageData.url] <br> 来自 [ImageData.url] 的 URL
     * @return The image [ByteArrayInputStream] <br> 图片字节输入流
     * @throws FuelError if download failed <br> 下载失败时抛出
     * @see ImageData
     */
    @Throws(FuelError::class)
    fun download(url: String): InputStream {
        if (PluginConfig.save) {
            var file: File? = null
            val (_, _, result) = FuelManager()
                .also {
                    try {
                        it.proxy = Proxy(
                            Utils.getProxyType(ProxyConfig.type),
                            InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
                        )
                    } catch (iae: IllegalArgumentException) { }
                }
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
        val (_, _, result) = FuelManager()
            .also {
                try {
                    it.proxy = Proxy(
                        Utils.getProxyType(ProxyConfig.type),
                        InetSocketAddress(ProxyConfig.hostname, ProxyConfig.port)
                    )
                } catch (iae: IllegalArgumentException) { }
            }
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
