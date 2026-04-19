package com.twix.database.poke

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface PokeHistoryDao {
    @Upsert
    suspend fun upsert(entity: PokeHistoryEntity)

    @Query("SELECT * FROM poke_history WHERE goalId = :goalId")
    suspend fun findByGoalId(goalId: Long): PokeHistoryEntity?
}
