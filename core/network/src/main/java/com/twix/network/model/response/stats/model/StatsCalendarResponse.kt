package com.twix.network.model.response.stats.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsCalendarResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val goalName: String,
    @SerialName("goalIcon") val goalIcon: String,
    @SerialName("yearMonth") val yearMonth: String,
    @SerialName("isCompleted") val isCompleted: Boolean,
    @SerialName("completedDates") val completedDates: List<CompletedDateResponse>,
)

@Serializable
data class CompletedDateResponse(
    @SerialName("date") val date: String,
    @SerialName("myImageUrl") val myImageUrl: String?,
    @SerialName("partnerImageUrl") val partnerImageUrl: String?,
)
