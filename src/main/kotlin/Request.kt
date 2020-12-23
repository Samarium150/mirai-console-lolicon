package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

data class Request(
    val apikey: String,
    val keyword: String,
    val r18: Boolean = false,
    val num: Int = 1,
    val proxy: String = "i.pixiv.cat",
    val size1200: Boolean = true
) {
    override fun toString(): String {
        var result = ""
        for (props in Request::class.memberProperties) {
            val value = props.get(this)
            if (value == "") continue
            result += "${props.name}=${value}&"
        }
        return result.dropLast(1)
    }

    fun toReadable(): String {
        return (
            "apikey: ${apikey}, " +
            "keyword: \"${keyword}\", " +
            "r18: ${r18}, " +
            "num: $num"
        )
    }
}