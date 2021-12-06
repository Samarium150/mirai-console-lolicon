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

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * Object for auto saving plugin configuration
 * <br>
 * 插件配置
 *
 * @constructor Create a plugin config instance <br> 实例化插件配置
 * @see net.mamoe.mirai.console.data.AutoSavePluginConfig
 */
object PluginConfig : AutoSavePluginConfig("Config") {

    /**
     * Bot owner's id
     * <br>
     * Bot 所有者账号
     */
    @ValueDescription("Bot所有者账号")
    val master: Long by value()

    /**
     * mode for Get and adv command
     * <br>
     * Get命令和Adv命令的模式
     */
    @ValueDescription("Get命令和Adv命令的模式：none/whitelist/blacklist")
    val mode: String by value("none")

    /**
     * Enable image saving
     * <br>
     * 是否保存图片
     */
    @ValueDescription("是否保存图片")
    val save: Boolean by value(false)

    /**
     * Enable caching
     * <br>
     * 是否使用已保存的图片作为缓存
     */
    @ValueDescription("是否使用已保存的图片作为缓存")
    val cache: Boolean by value(false)

    /**
     * Sending image info
     * <br>
     * 是否发送图片信息
     */
    @ValueDescription("是否发送图片信息")
    val verbose: Boolean by value(true)

    /**
     * Enable flash image
     * <br>
     * 是否启用闪照模式
     */
    @ValueDescription("是否启用闪照模式")
    val flash: Boolean by value(false)

    /**
     * Size of the picture
     * <br>
     * 图片大小
     */
    @ValueDescription("original/regular/small/thumb/mini")
    val size: String by value("regular")

    /**
     * Custom proxy
     * <br>
     * 自定义代理
     */
    @ValueDescription("获取Pixiv图片的代理")
    val proxy: String by value("i.pixiv.re")

    /**
     * Default recall time
     * <br>
     * 默认的撤回时间(单位: s)
     */
    @ValueDescription("默认的撤回时间(单位：s)")
    var recall: Int by value(30)

    /**
     * Enable recalling image information
     * <br>
     * 是否撤回图片信息
     */
    @ValueDescription("是否撤回图片信息")
    var recallImgInfo: Boolean by value(false)

    /**
     * Enable recalling image
     * <br>
     * 是否撤回图片
     */
    @ValueDescription("是否撤回图片")
    var recallImg: Boolean by value(true)

    /**
     * Default cooldown time
     * <br>
     * 默认的冷却时间(单位: s)
     */
    @ValueDescription("默认的冷却时间(单位：s)")
    var cooldown: Int by value(60)

    /**
     * Tag filter mode
     * <br>
     * 标签过滤模式
     */
    @ValueDescription("标签过滤模式: none/whitelist/blacklist")
    val tagFilterMode: String by value("none")

    /**
     * Tag filter
     * <br>
     * 标签过滤器
     */
    @ValueDescription("标签过滤器，白名单模式只发送包含指定标签的图片，黑名单模式只发送不包含指定标签的图片。(仅Get命令，Adv命令中不生效)")
    val tagFilter: MutableSet<String> by value()
}
