package com.github.samarium150.mirai.plugin

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
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
     */
    fun get(request: Request): Response {
        val url = "https://api.lolicon.app/setu/?$request"
        val (_, response, result) = getResponse(url)
        if (result is Result.Failure) throw result.getException()
        val feedback: Response = gson.fromJson(String(response.data), Response::class.java)
        if (feedback.code != 0) throw APIError(feedback.code, feedback.msg)
        return feedback
    }

    /**
     * Downloads the image from [url]
     *
     * @param url [String] URL from [ImageData.url]
     * @return [InputStream] The image [ByteArrayInputStream]
     */
    fun download(url: String): InputStream {
        val (_, response, result) = getResponse(url)
        if (result is Result.Failure) throw result.getException()
        return ByteArrayInputStream(response.data)
    }
}