package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

/**
 * The image information returned by Lolicon API
 *
 * @property pid [Int] Image PID
 * @property p [Int] the page of the image
 * @property uid [Int] Author UID
 * @property title [String] Image title
 * @property author [String] Author's name
 * @property url [String] Link to the image
 * @property r18 [Boolean] R18 category
 * @property width [Int] Width of the image
 * @property height [Int] Height of the image
 * @property tags [ArrayList]<[String]> Image tags
 * @constructor Creates an ImageData instance
 */
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
    /**
     * Returns the string representation
     *
     * @return [String] Representation of the data class
     */
    override fun toString(): String {
        var result = "ImageData({"
        for (props in ImageData::class.memberProperties) {
            result += "${props.name}: ${props.get(this)}, "
        }
        result = result.dropLast(2) + "})"
        return result
    }

    /**
     * Returns the readable information
     *
     * @return [String] Useful and readable information for users
     */
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