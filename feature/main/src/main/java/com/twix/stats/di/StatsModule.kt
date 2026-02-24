package com.twix.stats.di

import com.twix.stats.StatsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val statsModule =
    module {
        viewModelOf(::StatsViewModel)
    }
