/*
 * Copyright (c) 2020-2023 Samarium
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
package io.github.samarium150.mirai.plugin.lolicon.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

object PluginConfig : AutoSavePluginConfig("PluginConfig") {

    enum class Mode {
        None,
        Whitelist,
        Blacklist
    }

    @Suppress("unused")
    enum class Size {
        Original,
        Regular,
        Small,
        Thumb,
        Mini
    }

    enum class Type {
        Simple,
        Flash,
        Forward
    }

    @ValueDescription("是否保存图片")
    val save: Boolean by value(false)

    @ValueDescription("是否使用已保存的图片作为缓存")
    val cache: Boolean by value(false)

    @ValueDescription("是否提示Get和Adv命令已受理")
    val notify: Boolean by value(true)

    @ValueDescription("是否发送图片信息")
    val verbose: Boolean by value(true)

    @ValueDescription("图片发送模式: Simple/Flash/Forward")
    val messageType: Type by value(Type.Forward)

    @ValueDescription("图片大小: Original/Regular/Small/Thumb/Mini")
    val size: Size by value(Size.Regular)

    @ValueDescription("获取Pixiv图片的反向代理")
    val proxy: String by value("https://i.pixiv.re")

    @ValueDescription("默认的撤回时间(单位：s)")
    val recall: Int by value(30)

    @ValueDescription("是否撤回图片信息")
    val recallImgInfo: Boolean by value(false)

    @ValueDescription("是否撤回图片")
    val recallImg: Boolean by value(true)

    @ValueDescription("默认的冷却时间(单位：s)")
    val cooldown: Int by value(60)

    @ValueDescription("标签过滤模式: None/Whitelist/Blacklist")
    val tagFilterMode: Mode by value(Mode.None)

    @ValueDescription("标签过滤器，白名单模式只发送包含指定标签的图片，黑名单模式只发送不包含指定标签的图片。(仅Get命令，Adv命令中不生效)")
    val tagFilter: MutableSet<String> by value()
}
