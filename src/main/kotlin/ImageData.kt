package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

data class ImageData (
    val pid: Int,
    val p: Int,
    val uid: Int,
    val title: String,
    val author: String,
    val url: String,
    val r18: Boolean,
    val width: Int,
    val height: Int,
    val tags: ArrayList<String>
) {
    override fun toString(): String {
        var result = "ImageData({"
        for (props in ImageData::class.memberProperties) {
            result += "${props.name}: ${props.get(this)}, "
        }
        result = result.dropLast(2) + "})"
        return result
    }

    fun toReadable(): String {
        return(
            "pid: ${pid}\n" +
            "标题: ${title}\n" +
            "作者: ${author}\n" +
            "标签: ${tags}\n" +
            "链接: $url"
        )
    }
}