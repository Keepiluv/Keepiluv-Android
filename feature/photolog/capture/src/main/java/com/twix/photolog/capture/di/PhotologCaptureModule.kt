package com.twix.photolog.capture.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.capture.PhotologCaptureViewModel
import com.twix.photolog.capture.model.camera.Camera
import com.twix.photolog.capture.model.camera.CaptureCamera
import com.twix.photolog.capture.navigation.PhotologCaptureNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val photologCaptureModule =
    module {
        viewModelOf(::PhotologCaptureViewModel)
        factory<Camera> { CaptureCamera(get()) }
        single<NavGraphContributor>(named(NavRoutes.PhotologRoute.route)) { PhotologCaptureNavGraph }
    }
