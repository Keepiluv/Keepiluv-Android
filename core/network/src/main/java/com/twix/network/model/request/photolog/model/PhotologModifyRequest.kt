package com.twix.network.model.request.photolog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotologModifyRequest(
    @SerialName("fileName") val fileName: String,
    @SerialName("comment") val comment: String,
)
