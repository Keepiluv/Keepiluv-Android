package com.twix.analytics.di

import com.google.firebase.analytics.FirebaseAnalytics
import com.twix.analytics.AnalyticsLogger
import com.twix.analytics.FirebaseAnalyticsLogger
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val analyticsModule =
    module {
        single { FirebaseAnalytics.getInstance(androidContext()) }
        single<AnalyticsLogger> { FirebaseAnalyticsLogger(get()) }
    }
