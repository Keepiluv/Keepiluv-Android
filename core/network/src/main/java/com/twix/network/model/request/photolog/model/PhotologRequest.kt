package com.twix.network.model.request.photolog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotologRequest(
    @SerialName("goalId") val goalId: Long,
    @SerialName("fileName") val fileName: String,
    @SerialName("comment") val comment: String,
    @SerialName("verificationDate") val verificationDate: String,
)
