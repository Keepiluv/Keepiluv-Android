package com.twix.login.di

import com.twix.login.LoginViewModel
import com.twix.login.google.GoogleLoginProvider
import com.twix.login.kakao.KakaoLoginProvider
import com.twix.login.navigation.LoginNavGraph
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val loginModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.LoginGraph.route)) { LoginNavGraph }

        factory { GoogleLoginProvider(androidContext()) }

        factory { KakaoLoginProvider() }

        viewModelOf(::LoginViewModel)
    }
