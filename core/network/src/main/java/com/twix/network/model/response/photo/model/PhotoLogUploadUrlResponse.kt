package com.twix.network.model.response.photo.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLogUploadUrlResponse(
    @SerialName("uploadUrl") val uploadUrl: String,
    @SerialName("fileName") val fileName: String,
)
