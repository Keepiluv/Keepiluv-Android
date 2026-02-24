package com.twix.network.model.request.notification

import kotlinx.serialization.Serializable

@Serializable
data class TokenRequest(
    val token: String,
)
