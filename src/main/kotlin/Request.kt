package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

/**
 * The information for making a GET request
 *
 * @property apikey [String] Legal apikey
 * @property keyword [String] Keyword for fuzzy searching
 * @property r18 [Boolean] R18 category
 * @property num [Int] Number of results
 * @property proxy [String] Domain of [ImageData.url]
 * @property size1200 [Boolean] Enable master_1200 thumbnail
 * @constructor Creates a Request instance
 */
data class Request(
    val apikey: String,
    val keyword: String,
    val r18: Boolean = false,
    val num: Int = 1,
    val proxy: String = "i.pixiv.cat",
    val size1200: Boolean = true
) {
    /**
     * Returns the string representation of parameters
     * for GET request
     *
     * @return [String] Request parameters
     */
    override fun toString(): String {
        var result = ""
        for (props in Request::class.memberProperties) {
            val value = props.get(this)
            if (value == "") continue
            result += "${props.name}=${value}&"
        }
        return result.dropLast(1)
    }

    /**
     * Returns the readable information
     *
     * @return [String] Useful and readable information for logging
     */
    fun toReadable(): String {
        return (
            "apikey: ${apikey}, " +
            "keyword: \"${keyword}\", " +
            "r18: ${r18}, " +
            "num: $num"
        )
    }
}