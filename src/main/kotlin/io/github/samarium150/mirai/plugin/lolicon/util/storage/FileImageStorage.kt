package io.github.samarium150.mirai.plugin.lolicon.util.storage

import io.github.samarium150.mirai.plugin.lolicon.util.cacheFolder
import java.io.File

class FileImageStorage: AbstractImageStorage() {
    init {

    }
    override suspend fun load(url: String): ByteArray {
        val paths = url.split("/")
        val path = "$cacheFolder/${paths[paths.lastIndex]}"
        val cache = File(System.getProperty("user.dir") + path)
        return cache.readBytes()
    }

    override suspend fun save(url: String, bytes: ByteArray) {
        val urlPaths = url.split("/")
        val file = cacheFolder.resolve(urlPaths[urlPaths.lastIndex])
        file.writeBytes(bytes)
    }
}
