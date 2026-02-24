package com.yapp.stats.detail.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.yapp.detail.navigation.StatsDetailGraph
import org.koin.core.qualifier.named
import org.koin.dsl.module

val statsDetailModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.StatsDetailRoute.route)) { StatsDetailGraph }
    }
