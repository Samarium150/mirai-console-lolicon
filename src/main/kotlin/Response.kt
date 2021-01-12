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