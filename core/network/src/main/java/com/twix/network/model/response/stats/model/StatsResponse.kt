package com.twix.network.model.response.stats.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsResponse(
    @SerialName("selectedDate") val selectedDate: String,
    @SerialName("statsGoals") val statsGoals: List<StatsGoalResponse>,
)

@Serializable
data class StatsGoalResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val goalName: String,
    @SerialName("goalIconType") val goalIconType: String,
    @SerialName("monthlyTargetCount") val monthlyTargetCount: Int,
    @SerialName("stamp") val stamp: String,
    @SerialName("myStats") val myStats: ParticipantStatsResponse,
    @SerialName("partnerStats") val partnerStats: ParticipantStatsResponse,
)

@Serializable
data class ParticipantStatsResponse(
    @SerialName("nickname") val nickname: String,
    @SerialName("endCount") val endCount: Int,
    @SerialName("stampColors") val stampColors: List<String>,
)
