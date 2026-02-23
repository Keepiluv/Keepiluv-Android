package com.twix.domain.repository

import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photo.PhotoLogUploadInfo
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.result.AppResult

interface PhotoLogRepository {
    suspend fun getUploadUrl(goalId: Long): AppResult<PhotoLogUploadInfo>

    suspend fun uploadPhotoLog(photologParam: PhotologParam): AppResult<Unit>

    suspend fun uploadPhotoLogImage(
        goalId: Long,
        bytes: ByteArray,
        contentType: String,
    ): AppResult<String>

    suspend fun fetchPhotoLogs(targetDate: String): AppResult<PhotoLogs>

    suspend fun reactToPhotolog(
        photologId: Long,
        reaction: GoalReactionType,
    ): AppResult<Unit>
}
