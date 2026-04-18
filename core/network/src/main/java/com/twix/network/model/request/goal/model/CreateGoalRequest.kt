package com.twix.network.model.request.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateGoalRequest(
    @SerialName("goalName") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("repeatCount") val repeatCount: Int,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String?,
)
