package com.twix.data.repository

import com.twix.domain.model.poke.PokeResult
import com.twix.domain.repository.PokeRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.response.poke.mapper.toDomain
import com.twix.network.service.PokeService
import com.twix.result.AppResult

class DefaultPokeRepository(
    private val service: PokeService,
) : PokeRepository {
    override suspend fun pokeGoal(goalId: Long): AppResult<PokeResult> =
        safeApiCall {
            service.pokeGoal(goalId).toDomain()
        }
}
