package com.twix.photolog.detail.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.capture.di.photologCaptureModule
import com.twix.photolog.detail.PhotologDetailViewModel
import com.twix.photolog.detail.navigation.PhotologGraph
import com.twix.photolog.editor.di.photologEditorModule
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val photologDetailModule =
    module {
        includes(photologCaptureModule, photologEditorModule)
        viewModelOf(::PhotologDetailViewModel)
        single<NavGraphContributor>(named(NavRoutes.PhotologRoute.route)) { PhotologGraph }
    }
