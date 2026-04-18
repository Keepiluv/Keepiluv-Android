package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalSummaryListResponse(
    @SerialName("goals") val goals: List<GoalSummaryResponse>,
)
