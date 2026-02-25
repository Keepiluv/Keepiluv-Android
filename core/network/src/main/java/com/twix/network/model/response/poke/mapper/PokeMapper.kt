package com.twix.network.model.response.poke.mapper

import com.twix.domain.model.poke.PokeResult
import com.twix.network.model.response.poke.model.PokeResponse

fun PokeResponse.toDomain(): PokeResult =
    PokeResult(
        message = message,
    )
