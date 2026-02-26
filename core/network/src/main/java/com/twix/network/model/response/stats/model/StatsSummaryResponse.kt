package com.twix.network.model.response.stats.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class StatsSummaryResponse(
    @SerialName("myNickname") val myNickname: String,
    @SerialName("partnerNickname") val partnerNickname: String,
    @SerialName("totalCount") val totalCount: Int,
    @SerialName("myCompletedCount") val myCompletedCount: Int,
    @SerialName("partnerCompletedCount") val partnerCompletedCount: Int,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("startDate") val startDate: String,
    @SerialName("endDate") val endDate: String?,
)
