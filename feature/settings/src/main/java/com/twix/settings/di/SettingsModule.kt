package com.twix.settings.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.settings.SettingsViewModel
import com.twix.settings.navigation.SettingsNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val settingsModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.SettingsGraph.route)) { SettingsNavGraph }
        viewModelOf(::SettingsViewModel)
    }
