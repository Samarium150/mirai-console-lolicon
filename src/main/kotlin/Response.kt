package com.github.samarium150.mirai.plugin

import kotlin.reflect.full.memberProperties

/**
 * The response body returned by Lolicon API
 *
 * @property code [Int] Status code
 * @property msg [String] Error message
 * @property quota [Int] Quota remained
 * @property quota_min_ttl [Int] Minimal time for
 * @property count [Int] Number of results
 * @property data [ArrayList]<[ImageData]> Array of results
 * @constructor Creates a Response instance
 */
data class Response(
    val code: Int,
    val msg: String,
    val quota: Int,
    val quota_min_ttl: Int,
    val count: Int,
    val data: ArrayList<ImageData>
) {
    /**
     * Return the string representation of response body
     *
     * @return [String] Request parameters
     */
    override fun toString(): String {
        var result = "Response({"
        for (props in Response::class.memberProperties) {
            result += "${props.name}: ${props.get(this)}, "
        }
        result = result.dropLast(2) + "})"
        return result
    }

    /**
     * Return the readable information
     *
     * @return [String] Useful and readable information for logging
     */
    fun toReadable(): String {
        return "quota: ${quota}, quota_min_ttl: ${quota_min_ttl}, count: $count"
    }
}