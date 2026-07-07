package com.twix.data.repository

import com.twix.database.poke.PokeHistoryDao
import com.twix.database.poke.PokeHistoryEntity
import com.twix.domain.model.poke.PokeResult
import com.twix.domain.repository.PokeRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.response.poke.mapper.toDomain
import com.twix.network.service.PokeService
import com.twix.result.AppResult

class DefaultPokeRepository(
    private val service: PokeService,
    private val pokeHistoryDao: PokeHistoryDao,
) : PokeRepository {
    override suspend fun pokeGoal(goalId: Long): AppResult<PokeResult> =
        safeApiCall {
            service.pokeGoal(goalId).toDomain()
        }

    override suspend fun savePokeHistory(
        goalId: Long,
        targetDate: String,
        pokedAt: Long,
    ) {
        pokeHistoryDao.upsert(
            PokeHistoryEntity(
                goalId = goalId,
                targetDate = targetDate,
                pokedAt = pokedAt,
            ),
        )
    }

    override suspend fun findPokeHistory(
        goalId: Long,
        targetDate: String,
    ): Long? = pokeHistoryDao.findPokeHistoryEntity(goalId, targetDate)?.pokedAt
}
