package com.twix.data.di

import org.koin.dsl.module

val dataModule =
    module {
        includes(repositoryModule, useCaseModule)
    }
