package com.twix.database.poke

import androidx.room.Entity

@Entity(
    tableName = "poke_history",
    primaryKeys = ["goalId", "targetDate"],
)
data class PokeHistoryEntity(
    val goalId: Long,
    val targetDate: String,
    val pokedAt: Long,
)
