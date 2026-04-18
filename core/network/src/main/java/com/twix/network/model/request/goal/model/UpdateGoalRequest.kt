package com.twix.network.model.request.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateGoalRequest(
    @SerialName("goalName") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("repeatCount") val repeatCount: Int,
    @SerialName("endDate") val endDate: String? = null,
)
