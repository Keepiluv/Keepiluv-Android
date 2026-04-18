package com.twix.network.upload

import com.twix.network.execute.safeUploadCall
import com.twix.result.AppResult
import io.ktor.client.HttpClient
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess

class PresignedUploader(
    private val client: HttpClient,
) {
    suspend fun upload(
        uploadUrl: String,
        bytes: ByteArray,
        contentType: String = "image/jpeg",
    ): AppResult<Unit> =
        safeUploadCall {
            val response =
                client.put(uploadUrl) {
                    contentType(ContentType.parse(contentType))
                    setBody(bytes)
                }

            if (!response.status.isSuccess()) {
                throw IllegalStateException("S3 upload failed: ${response.status.value}")
            }
        }
}
