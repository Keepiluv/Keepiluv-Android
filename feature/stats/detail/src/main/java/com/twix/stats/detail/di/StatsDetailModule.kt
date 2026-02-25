package com.twix.stats.detail.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.stats.detail.navigation.StatsDetailGraph
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statsDetailModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.StatsDetailRoute.route)) { StatsDetailGraph }
    }
