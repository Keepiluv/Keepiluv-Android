package com.yapp.twix

import android.app.Application
import com.yapp.twix.di.initKoin

class TwixApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            context = this,
        )
    }
}
