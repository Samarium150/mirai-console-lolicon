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
 * <br>
 * 进行GET请求所需的信息
 *
 * @property apikey Legal apikey <br> 合法的apikey
 * @property keyword Keyword for fuzzy searching <br> 模糊搜索的关键词
 * @property r18 R18 category <br> R18 类别
 * @property num Number of results <br> 结果数量
 * @property proxy Domain of [ImageData.url] <br> 代理链接
 * @property size1200 Enable master_1200 thumbnail <br> 启用 master_1200 压缩
 * @constructor Create a Request parameters instance <br> 实例化请求参数
 */
data class RequestParams(
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
     * <br>
     * 返回用字符串表示的GET请求参数
     *
     * @return Request parameters <br> GET请求参数
     */
    override fun toString(): String {
        var result = ""
        for (props in RequestParams::class.memberProperties) {
            val value = props.get(this)
            if (value == "") continue
            result += "${props.name}=${value}&"
        }
        return if (size1200) result.dropLast(6) else result.dropLast(16)
    }

    /**
     * Return the readable information
     * <br>
     * 返回具有可读性的信息
     *
     * @return readable information <br> 具有可读性的信息
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
