package com.twix.network.model.response.onboarding

import kotlinx.serialization.Serializable

@Serializable
data class InviteCodeResponse(
    val inviteCode: String,
)
