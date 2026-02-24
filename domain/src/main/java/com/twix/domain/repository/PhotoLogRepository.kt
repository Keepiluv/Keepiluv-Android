package com.twix.domain.repository

import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photo.PhotoLogUploadInfo
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.result.AppResult
import java.time.LocalDate

interface PhotoLogRepository {
    suspend fun getUploadUrl(goalId: Long): AppResult<PhotoLogUploadInfo>

    suspend fun uploadPhotolog(photologParam: PhotologParam): AppResult<Unit>

    suspend fun uploadPhotologImage(
        goalId: Long,
        bytes: ByteArray,
        contentType: String,
    ): AppResult<String>

    suspend fun fetchPhotologs(targetDate: LocalDate): AppResult<PhotoLogs>

    suspend fun reactToPhotolog(
        photologId: Long,
        reaction: GoalReactionType,
    ): AppResult<Unit>

    suspend fun modifyPhotolog(
        photologId: Long,
        fileName: String,
        comment: String,
    ): AppResult<Unit>
}
