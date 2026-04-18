package com.twix.main.di

import com.twix.main.MainViewModel
import com.twix.main.navigation.MainNavGraph
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val mainModule =
    module {
        viewModelOf(::MainViewModel)
        single<NavGraphContributor>(named(NavRoutes.MainGraph.route)) { MainNavGraph }
    }
