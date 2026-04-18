package com.yapp.twix.di

import com.twix.designsystem.components.toast.ToastManager
import org.koin.dsl.module

val uiModule =
    module {
        single { ToastManager() }
    }
