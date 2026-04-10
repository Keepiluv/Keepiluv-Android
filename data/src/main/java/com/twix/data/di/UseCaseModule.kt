package com.twix.data.di

import com.twix.domain.usecase.PokeGoalUseCase
import org.koin.dsl.module

internal val useCaseModule =
    module {
        single { PokeGoalUseCase(get()) }
    }
