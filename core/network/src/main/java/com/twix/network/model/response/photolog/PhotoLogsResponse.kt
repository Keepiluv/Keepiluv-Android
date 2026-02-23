package com.twix.network.model.response.photolog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoLogsResponse(
    @SerialName("targetDate") val targetDate: String,
    @SerialName("myNickname") val myNickname: String,
    @SerialName("partnerNickname") val partnerNickname: String,
    @SerialName("photologs") val photologs: List<GoalPhotologResponse>,
)
