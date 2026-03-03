package com.twix.photolog.capture.di

import com.twix.photolog.capture.PhotologCaptureViewModel
import com.twix.photolog.capture.model.camera.Camera
import com.twix.photolog.capture.model.camera.CaptureCamera
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val photologCaptureModule =
    module {
        viewModelOf(::PhotologCaptureViewModel)
        factory<Camera> { CaptureCamera(get()) }
    }
