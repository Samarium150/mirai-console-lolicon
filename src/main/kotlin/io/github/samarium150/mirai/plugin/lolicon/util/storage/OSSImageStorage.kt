package io.github.samarium150.mirai.plugin.lolicon.util.storage

import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.model.GetObjectRequest
import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.StorageConfig

class OSSImageStorage: AbstractImageStorage() {
    private val client: OSS = OSSClientBuilder().build(StorageConfig.endpoint,
        StorageConfig.accessKey,
        StorageConfig.secretKey)

    private fun urlToPath(url: String): String {
        val path = url.split("/").drop(5).joinToString("/")
        return "lolicon/$path"
    }
    override suspend fun load(url: String): ByteArray {
        val request = GetObjectRequest(StorageConfig.bucket, urlToPath(url))
        return try {
            client.getObject(request).objectContent.readBytes()
        } catch (e: Exception) {
            MiraiConsoleLolicon.logger.error("Failed to load image from OSS", e)
            ByteArray(0)
        }
    }

    override suspend fun save(url: String, bytes: ByteArray) {
        try {
            client.putObject(StorageConfig.bucket, urlToPath(url), bytes.inputStream())
        } catch (e: Exception) {
            MiraiConsoleLolicon.logger.error("Failed to save image to OSS", e)
        }
    }
}
