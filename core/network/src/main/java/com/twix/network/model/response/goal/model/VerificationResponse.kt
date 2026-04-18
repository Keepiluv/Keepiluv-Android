package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerificationResponse(
    @SerialName("photologId") val photologId: Long,
    @SerialName("imageUrl") val imageUrl: String,
    @SerialName("comment") val comment: String? = null,
    @SerialName("reaction") val reaction: String? = null,
    @SerialName("uploadedAt") val uploadedAt: String, // yyyy-mm-dd
)
