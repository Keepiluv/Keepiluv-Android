package com.twix.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CoupleConnectionRequest(
    val inviteCode: String,
)
