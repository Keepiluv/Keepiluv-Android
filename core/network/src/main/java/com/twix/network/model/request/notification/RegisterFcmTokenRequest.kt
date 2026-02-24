package com.twix.network.model.request.notification

import kotlinx.serialization.Serializable

@Serializable
data class RegisterFcmTokenRequest(
    val token: String,
    val deviceId: String,
)
