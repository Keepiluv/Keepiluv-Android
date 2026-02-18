package com.twix.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ReactionRequest(
    val reaction: String,
)
