package com.twix.login.di

import com.twix.domain.login.LoginProvider
import com.twix.domain.model.enums.LoginType
import com.twix.login.LoginProviderFactory
import com.twix.login.LoginViewModel
import com.twix.login.google.GoogleLoginProvider
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

        single<LoginProvider>(named(LoginType.GOOGLE.name)) {
            GoogleLoginProvider(androidContext())
        }

        single {
            LoginProviderFactory(
                mapOf(
                    LoginType.GOOGLE to get<LoginProvider>(named(LoginType.GOOGLE.name)),
                ),
            )
        }

        viewModelOf(::LoginViewModel)
    }
