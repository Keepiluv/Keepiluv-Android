package com.twix.util.di

import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.TaskCertificationRefreshBus
import org.koin.dsl.module

val utilModule =
    module {
        single { GoalRefreshBus() }
        single { TaskCertificationRefreshBus() }
    }
