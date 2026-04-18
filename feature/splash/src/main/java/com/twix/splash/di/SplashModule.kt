package com.twix.splash.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.splash.SplashViewModel
import com.twix.splash.navigation.SplashNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val splashModule =
    module {
        viewModelOf(::SplashViewModel)
        single<NavGraphContributor>(named(NavRoutes.SplashGraph.route)) { SplashNavGraph }
    }
