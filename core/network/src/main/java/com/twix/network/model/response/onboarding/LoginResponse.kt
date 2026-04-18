package com.twix.network.model.response.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class LoginResponse(
    val userId: Long,
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean,
)
