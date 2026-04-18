package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalSummaryResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String?,
)
