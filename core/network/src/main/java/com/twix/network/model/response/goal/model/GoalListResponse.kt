package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalListResponse(
    @SerialName("completedCount") val completedCount: Int,
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("goals") val goals: List<GoalResponse>,
)
