package com.twix.database.di

import androidx.room.Room
import com.twix.database.TwixDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            Room
                .databaseBuilder(
                    androidContext(),
                    TwixDatabase::class.java,
                    "twix-database",
                ).addMigrations(TwixDatabase.MIGRATION_1_2)
                .build()
        }
        single { get<TwixDatabase>().pokeHistoryDao() }
    }
