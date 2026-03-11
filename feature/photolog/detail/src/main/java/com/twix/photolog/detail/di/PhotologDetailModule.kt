package com.twix.photolog.detail.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.detail.PhotologDetailViewModel
import com.twix.photolog.detail.navigation.PhotologDetailNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val photologDetailModule =
    module {
        viewModelOf(::PhotologDetailViewModel)
        single<NavGraphContributor>(named(NavRoutes.PhotologDetailRoute.route)) { PhotologDetailNavGraph }
    }
