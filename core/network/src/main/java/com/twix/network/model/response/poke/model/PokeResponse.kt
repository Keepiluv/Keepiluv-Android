package com.twix.network.model.response.poke.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PokeResponse(
    @SerialName("message") val message: String,
)
