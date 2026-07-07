package com.twix.domain.repository

import com.twix.domain.model.poke.PokeResult
import com.twix.result.AppResult

interface PokeRepository {
    suspend fun pokeGoal(goalId: Long): AppResult<PokeResult>

    suspend fun savePokeHistory(
        goalId: Long,
        targetDate: String,
        pokedAt: Long,
    )

    suspend fun findPokeHistory(
        goalId: Long,
        targetDate: String,
    ): Long?
}
