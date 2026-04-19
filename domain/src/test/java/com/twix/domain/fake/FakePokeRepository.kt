package com.twix.domain.fake

import com.twix.domain.model.poke.PokeResult
import com.twix.domain.repository.PokeRepository
import com.twix.result.AppResult

class FakePokeRepository : PokeRepository {
    val pokeHistory: MutableMap<Long, Long?> = mutableMapOf()
    val savedPokeHistory: MutableMap<Long, Long> = mutableMapOf()
    var pokeGoalResult: AppResult<PokeResult> = AppResult.Success(PokeResult(message = ""))
    var pokeGoalCallCount: Int = 0

    override suspend fun pokeGoal(goalId: Long): AppResult<PokeResult> {
        pokeGoalCallCount++
        return pokeGoalResult
    }

    override suspend fun savePokeHistory(
        goalId: Long,
        pokedAt: Long,
    ) {
        savedPokeHistory[goalId] = pokedAt
    }

    override suspend fun findPokeHistory(goalId: Long): Long? = pokeHistory[goalId]
}
