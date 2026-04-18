package com.twix.network.model.response.photo.mapper

import com.twix.domain.model.photo.PhotoLogUploadInfo
import com.twix.network.model.response.photo.model.PhotoLogUploadUrlResponse

fun PhotoLogUploadUrlResponse.toDomain(): PhotoLogUploadInfo =
    PhotoLogUploadInfo(
        uploadUrl = this.uploadUrl,
        fileName = this.fileName,
    )
