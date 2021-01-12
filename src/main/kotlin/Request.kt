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
 * The information for making a GET request
 *
 * @property apikey [String] Legal apikey
 * @property keyword [String] Keyword for fuzzy searching
 * @property r18 [Int] R18 category
 * @property num [Int] Number of results
 * @property proxy [String] Domain of [ImageData.url]
 * @property size1200 [Boolean] Enable master_1200 thumbnail
 * @constructor Creates a Request instance
 */
data class Request(
    val apikey: String,
    val keyword: String,
    val r18: Int = 0,
    val num: Int = 1,
    val proxy: String = "i.pixiv.cat",
    val size1200: Boolean = true
) {
    /**
     * Return the string representation of parameters
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
     * Return the readable information
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