package com.twix.util.di

import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.StatsDetailRefreshBus
import com.twix.util.bus.StatsRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import org.koin.dsl.module

val utilModule =
    module {
        single { GoalRefreshBus() }
        single { PhotologRefreshBus() }
        single { StatsRefreshBus() }
        single { StatsDetailRefreshBus() }
    }
