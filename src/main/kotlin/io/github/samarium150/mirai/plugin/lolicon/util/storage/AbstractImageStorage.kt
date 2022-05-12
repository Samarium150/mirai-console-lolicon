package io.github.samarium150.mirai.plugin.lolicon.util.storage

import io.github.samarium150.mirai.plugin.lolicon.config.PluginConfig
import io.github.samarium150.mirai.plugin.lolicon.util.downloadImage
import java.io.InputStream

/**
 * 抽象一个存储图片的Class，具有存储和读取图片的功能
 */
abstract class AbstractImageStorage {
    abstract suspend fun load(url: String): ByteArray
    abstract suspend fun save(url: String, bytes: ByteArray)
    suspend fun getImageWithCache(url: String): InputStream {
        val bytes = if (PluginConfig.cache) {
            try {
                load(url)
            } catch (e: Exception) {
                // 如果没有缓存，则下载图片
                downloadImage(url)
            }
        } else {
            downloadImage(url)
        }
        if (PluginConfig.save) {
            save(url, bytes)
        }
        return bytes.inputStream()
    }
}
