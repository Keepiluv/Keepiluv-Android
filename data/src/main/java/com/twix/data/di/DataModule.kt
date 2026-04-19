package com.twix.data.di

import com.twix.database.di.databaseModule

val dataModule =
    listOf(
        databaseModule,
        repositoryModule,
        useCaseModule,
    )
