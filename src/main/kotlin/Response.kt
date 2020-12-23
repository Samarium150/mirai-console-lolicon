package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

data class Response(
    val code: Int,
    val msg: String,
    val quota: Int,
    val quotaMinTtl: Int,
    val count: Int,
    val data: ArrayList<ImageData>
) {
    override fun toString(): String {
        var result = "Response({"
        for (props in Response::class.memberProperties) {
            result += "${props.name}: ${props.get(this)}, "
        }
        result = result.dropLast(2) + "})"
        return result
    }

    fun toReadable(): String {
        return "quota: ${quota}, quotaMinTtl: ${quotaMinTtl}, count: $count"
    }
}