package com.twix.network.model.response.photolog

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalPhotologResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val goalName: String,
    @SerialName("goalIcon") val goalIcon: String,
    @SerialName("myPhotolog") val myPhotolog: PhotologDetailResponse? = null,
    @SerialName("partnerPhotolog") val partnerPhotolog: PhotologDetailResponse? = null,
)
