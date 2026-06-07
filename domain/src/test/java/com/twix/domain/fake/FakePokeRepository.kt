package com.twix.domain.fake

import com.twix.domain.model.poke.PokeResult
import com.twix.domain.repository.PokeRepository
import com.twix.result.AppResult

class FakePokeRepository : PokeRepository {
    val pokeHistory: MutableMap<PokeHistoryKey, Long?> = mutableMapOf()
    val savedPokeHistory: MutableMap<PokeHistoryKey, Long> = mutableMapOf()
    var pokeGoalResult: AppResult<PokeResult> = AppResult.Success(PokeResult(message = ""))
    var pokeGoalCallCount: Int = 0

    override suspend fun pokeGoal(goalId: Long): AppResult<PokeResult> {
        pokeGoalCallCount++
        return pokeGoalResult
    }

    override suspend fun savePokeHistory(
        goalId: Long,
        targetDate: String,
        pokedAt: Long,
    ) {
        savedPokeHistory[PokeHistoryKey(goalId, targetDate)] = pokedAt
    }

    override suspend fun findPokeHistory(
        goalId: Long,
        targetDate: String,
    ): Long? = pokeHistory[PokeHistoryKey(goalId, targetDate)]

    data class PokeHistoryKey(
        val goalId: Long,
        val targetDate: String,
    )
}
