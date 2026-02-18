package com.twix.data.repository

import com.twix.domain.model.photo.PhotoLogUploadInfo
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.repository.PhotoLogRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.photolog.mapper.toRequest
import com.twix.network.model.response.photo.mapper.toDomain
import com.twix.network.model.response.photolog.mapper.toDomain
import com.twix.network.service.PhotoLogService
import com.twix.network.upload.PresignedUploader
import com.twix.result.AppResult

class DefaultPhotoLogRepository(
    private val service: PhotoLogService,
    private val uploader: PresignedUploader,
) : PhotoLogRepository {
    override suspend fun getUploadUrl(goalId: Long): AppResult<PhotoLogUploadInfo> = safeApiCall { service.getUploadUrl(goalId).toDomain() }

    override suspend fun uploadPhotoLog(photologParam: PhotologParam): AppResult<Unit> =
        safeApiCall { service.uploadPhotoLog(photologParam.toRequest()) }

    override suspend fun uploadPhotoLogImage(
        goalId: Long,
        bytes: ByteArray,
        contentType: String,
    ): AppResult<String> {
        // 서버에서 presigned url 발급
        val infoResult = getUploadUrl(goalId)
        val info =
            when (infoResult) {
                is AppResult.Success -> infoResult.data
                is AppResult.Error -> return infoResult
            }

        // S3로 직접 업로드
        val uploadResult =
            uploader.upload(
                uploadUrl = info.uploadUrl,
                bytes = bytes,
                contentType = contentType,
            )
        if (uploadResult is AppResult.Error) return uploadResult

        // fileName을 key로 사용함. 인증샷 등록 API에서 사용
        return AppResult.Success(info.fileName)
    }

    override suspend fun fetchPhotoLogs(targetDate: String): AppResult<PhotoLogs> =
        safeApiCall {
            service.fetchPhotoLogs(targetDate).toDomain()
        }
}
