package com.github.samarium150.mirai.plugin

/**
 * Class representation for handling Lolicon API error
 *
 * @property code [Int] Error code
 * @property message [String] Error message
 * @constructor
 */
class APIError internal constructor(
    private val code: Int,
    override val message: String
): Exception(message) {

    /**
     * Override toString() for logging
     *
     * @return [String]
     */
    override fun toString(): String {
        return "[${code}: ${message}]"
    }

    /**
     * Return the readable information
     *
     * @return [String]
     */
    fun toReadable(): String {
        return when (code) {
            -1, 401 -> message
            403 -> "调用不规范, 用户两行泪. 快去找开发者的麻烦."
            404 -> "没有找到相关图片, 换个关键词试试吧"
            429 -> "调用达到上限, 是时候换个apikey了"
            else -> throw Exception(this.toString())
        }
    }
}