package com.twix.onboarding.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.onboarding.OnBoardingViewModel
import com.twix.onboarding.navigation.OnboardingNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val onBoardingModule =
    module {
        viewModelOf(::OnBoardingViewModel)
        single<NavGraphContributor>(named(NavRoutes.OnboardingRoute.route)) { OnboardingNavGraph }
    }
