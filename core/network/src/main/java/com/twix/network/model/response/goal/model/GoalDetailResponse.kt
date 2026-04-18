package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalDetailResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("repeatCount") val repeatCount: Int,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String?,
    @SerialName("goalStatus") val goalStatus: String,
    @SerialName("createdAt") val createdAt: String,
)
