package com.twix.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ProfileRequest(
    val nickname: String,
)
