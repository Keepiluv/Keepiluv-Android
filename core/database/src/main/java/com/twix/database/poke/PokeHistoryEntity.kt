package com.twix.database.poke

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "poke_history")
data class PokeHistoryEntity(
    @PrimaryKey val goalId: Long,
    val pokedAt: Long,
)
