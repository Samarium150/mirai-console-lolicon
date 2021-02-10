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
 * <br>
 * Lolicon API 返回的响应
 *
 * @property code Status code <br> 状态码
 * @property msg Error message <br> 错误信息
 * @property quota Quota remained <br> 剩余配额
 * @property quota_min_ttl Minimal time for recovering all quota <br> 配额完全恢复需要的时间
 * @property count Number of results <br> 结果数量
 * @property data Array of results <br> 结果列表
 * @constructor Creates a Response instance <br> 实例化响应
 * @see ImageData
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
     * Return the string representation
     * <br>
     * 字符串化该类
     *
     * @return the string representation <br> 类的字符串表示
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
     * <br>
     * 返回具有可读性的信息
     *
     * @return readable information <br> 具有可读性的信息
     */
    fun toReadable(): String {
        return "quota: ${quota}, quota_min_ttl: ${quota_min_ttl}, count: $count"
    }
}
