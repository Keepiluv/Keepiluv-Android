package com.twix.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.twix.database.poke.PokeHistoryDao
import com.twix.database.poke.PokeHistoryEntity

@Database(
    entities = [PokeHistoryEntity::class],
    version = 2,
)
abstract class TwixDatabase : RoomDatabase() {
    abstract fun pokeHistoryDao(): PokeHistoryDao

    companion object {
        val MIGRATION_1_2 =
            object : Migration(1, 2) {
                override fun migrate(db: SupportSQLiteDatabase) {
                    db.execSQL("DROP TABLE IF EXISTS poke_history")
                    db.execSQL(
                        """
                        CREATE TABLE IF NOT EXISTS poke_history (
                            goalId INTEGER NOT NULL,
                            targetDate TEXT NOT NULL,
                            pokedAt INTEGER NOT NULL,
                            PRIMARY KEY(goalId, targetDate)
                        )
                        """.trimIndent(),
                    )
                }
            }
    }
}
