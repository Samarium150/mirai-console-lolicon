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

/**
 * Class representation for handling Lolicon API Exceptions
 * <br>
 * Lolicon API 异常
 *
 * @property code status code from HTTP response <br> HTTP请求返回状态码
 * @property message message from HTTP response <br> HTTP请求返回的信息
 * @constructor Create an Exception instance <br> 实例化异常
 * @see Response
 */
class APIException internal constructor(
    private val code: Int,
    override val message: String
) : Exception(message) {

    /**
     * Override toString() for logging
     * <br>
     * 复写 toString() 方法来输出日志
     *
     * @return log info <br> 日志信息
     */
    override fun toString(): String {
        return "[${code}: ${message}]"
    }

    /**
     * Return the readable information
     * <br>
     * 返回具有可读性的信息
     *
     * @return readable information <br> 具有可读性的信息
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
