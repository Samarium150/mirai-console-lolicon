package com.github.samarium150.mirai.plugin

import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.core.ResponseResultOf
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import java.io.ByteArrayInputStream
import java.io.InputStream

object RequestHandler {

    private val gson = Gson()

    private val getResponse: (String) -> (ResponseResultOf<String>) = { url -> Fuel.get(url).responseString() }

    fun get(request: Request): Response {
        val url = "https://api.lolicon.app/setu/?$request"
        val (_, response, result) = getResponse(url)
        if (result is Result.Failure) throw result.getException()
        val feedback: Response = gson.fromJson(String(response.data), Response::class.java)
        if (feedback.code != 0) throw Exception(feedback.msg)
        return feedback
    }

    fun download(url: String): InputStream {
        val (_, response, result) = getResponse(url)
        if (result is Result.Failure) throw result.getException()
        return ByteArrayInputStream(response.data)
    }
}