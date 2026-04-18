package com.twix.network.model.response.goal.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GoalResponse(
    @SerialName("goalId") val goalId: Long,
    @SerialName("goalName") val name: String,
    @SerialName("icon") val icon: String,
    @SerialName("repeatCycle") val repeatCycle: String,
    @SerialName("myCompleted") val myCompleted: Boolean,
    @SerialName("partnerCompleted") val partnerCompleted: Boolean,
    @SerialName("myVerification") val myVerification: VerificationResponse? = null,
    @SerialName("partnerVerification") val partnerVerification: VerificationResponse? = null,
)
