package com.twix.network.model.error

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val status: Int? = null,
    val code: String? = null,
    val message: String? = null,
)
