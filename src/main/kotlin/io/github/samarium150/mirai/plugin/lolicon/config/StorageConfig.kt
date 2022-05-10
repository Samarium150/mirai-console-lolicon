package io.github.samarium150.mirai.plugin.lolicon.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value

/**
 * 用于存储图片的配置
 *
 * @constructor 实例化存储图片的配置
 * @see AutoSavePluginConfig
 */
object StorageConfig : AutoSavePluginConfig("StorageConfig") {
    /**
     * 存储类型
     */
    @ValueDescription("可选：FILE/S3/OSS")
    val type: String by value("FILE")

    val accessKey: String by value("")
    val secretKey: String by value("")
    val bucket: String by value("")
    val endpoint: String by value("")
    val region: String by value("")
}
