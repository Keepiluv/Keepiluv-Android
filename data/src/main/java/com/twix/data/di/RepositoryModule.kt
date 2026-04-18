package com.twix.data.di

import com.twix.data.repository.DefaultPokeRepository
import com.twix.domain.repository.PokeRepository
import org.koin.dsl.module

internal val repositoryModule =
    module {
        single<PokeRepository> {
            DefaultPokeRepository(get(), get())
        }
    }
