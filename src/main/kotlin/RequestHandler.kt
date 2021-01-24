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
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * Object for handling GET request
 */
object RequestHandler {

    /**
     * The Gson instance
     */
    private val gson = Gson()

    /**
     * The lambda function for making GET request
     */
    private val getResponse: (String) -> ResponseResultOf<String> = { url -> Fuel.get(url).responseString() }

    /**
     * Makes a GET request to Lolicon API
     * with the given [request] parameters
     *
     * @param request [Request]
     * @return [Response]
     * @throws FuelError if GET request is failed
     * @throws JsonSyntaxException if returned JSON is invalid
     * @throws APIException if Lolicon API didn't return status 0
     */
    @Throws(FuelError::class, JsonSyntaxException::class, APIException::class)
    fun get(request: Request): Response {
        val url = "https://api.lolicon.app/setu/?$request"
        val (_, response, result) = getResponse(url)
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
        val (_, response, result) = getResponse(url)
        if (result is Result.Failure) throw result.getException()
        return ByteArrayInputStream(response.data)
    }
}
