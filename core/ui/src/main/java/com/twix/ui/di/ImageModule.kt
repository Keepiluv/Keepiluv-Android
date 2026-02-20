package com.twix.ui.di

import com.twix.ui.image.ImageGenerator
import com.twix.ui.image.Rotator
import org.koin.dsl.module

val imageModule =
    module {
        factory { Rotator(get()) }
        factory { ImageGenerator(get(), get<Rotator>()) }
    }
