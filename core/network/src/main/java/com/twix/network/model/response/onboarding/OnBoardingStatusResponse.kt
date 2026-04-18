package com.twix.network.model.response.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class OnBoardingStatusResponse(
    val status: String,
)
