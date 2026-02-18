package com.twix.network.model.response.photolog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotologDetailResponse(
    @SerialName("photologId") val photologId: Long,
    @SerialName("goalId") val goalId: Long,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("comment") val comment: String?,
    @SerialName("verificationDate") val verificationDate: String,
    @SerialName("uploaderName") val uploaderName: String,
    @SerialName("uploadedAt") val uploadedAt: String,
    @SerialName("reaction") val reaction: String?,
)
