package com.twix.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.twix.database.poke.PokeHistoryDao
import com.twix.database.poke.PokeHistoryEntity

@Database(
    entities = [PokeHistoryEntity::class],
    version = 1,
)
abstract class TwixDatabase : RoomDatabase() {
    abstract fun pokeHistoryDao(): PokeHistoryDao
}
