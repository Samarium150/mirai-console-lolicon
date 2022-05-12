package io.github.samarium150.mirai.plugin.lolicon.util.storage

import aws.sdk.kotlin.runtime.endpoint.AwsEndpoint
import aws.sdk.kotlin.runtime.endpoint.AwsEndpointResolver
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.smithy.kotlin.runtime.auth.awscredentials.Credentials
import aws.smithy.kotlin.runtime.auth.awscredentials.CredentialsProvider
import aws.smithy.kotlin.runtime.content.ByteStream
import aws.smithy.kotlin.runtime.content.toByteArray
import aws.smithy.kotlin.runtime.http.endpoints.Endpoint
import io.github.samarium150.mirai.plugin.lolicon.MiraiConsoleLolicon
import io.github.samarium150.mirai.plugin.lolicon.config.StorageConfig

class S3ImageStorage: AbstractImageStorage() {

    private val client: S3Client
    init {
        // init client with endpoint,ak/sk,region
        client = S3Client{
            region = StorageConfig.region
            credentialsProvider = object : CredentialsProvider {
                override suspend fun getCredentials(): Credentials = Credentials(
                    accessKeyId = StorageConfig.accessKey,
                    secretAccessKey = StorageConfig.secretKey,
                    sessionToken = "",
                    providerName = ""
                )
            }
            endpointResolver = AwsEndpointResolver { _, region ->
                if (region != "") {
                    AwsEndpoint(Endpoint("https://s3.$region.amazonaws.com"))
                }else {
                    AwsEndpoint(Endpoint(StorageConfig.endpoint))
                }
            }
        }
    }
    // url: https://i.pixiv.cat/img-original/img/2022/02/24/20/00/09/96492929_p0.png
    // output: lolicon/2022/02/24/20/00/09/96492929_p0.png
    private fun urlToPath(url: String): String {
        val path = url.split("/").drop(5).joinToString("/")
        return "lolicon/$path"
    }

    override suspend fun load(url: String): ByteArray {
        val bs =  client.getObject(GetObjectRequest{
            bucket = StorageConfig.bucket
            key = urlToPath(url)
        } ) {
            resp -> resp.body as ByteStream
        }
        return bs.toByteArray()
    }

    override suspend fun save(url: String, bytes: ByteArray) {
        try {
            val request = PutObjectRequest {
                bucket = StorageConfig.bucket
                key = urlToPath(url)
                body = ByteStream.fromBytes(bytes)
                metadata = mapOf("url" to url)
            }
            val resp = client.putObject(request)
            MiraiConsoleLolicon.logger.debug("S3ImageStorage: ${resp.eTag}")
        } catch (e: Exception) {
            MiraiConsoleLolicon.logger.error("S3ImageStorage.save", e)
        }
    }
}


