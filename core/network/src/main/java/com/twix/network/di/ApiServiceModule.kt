package com.twix.network.di

import com.twix.network.service.AuthService
import com.twix.network.service.GoalService
import com.twix.network.service.OnboardingService
import com.twix.network.service.PhotoLogService
import com.twix.network.service.StatsService
import com.twix.network.service.UserService
import com.twix.network.service.createAuthService
import com.twix.network.service.createGoalService
import com.twix.network.service.createOnboardingService
import com.twix.network.service.createPhotoLogService
import com.twix.network.service.createStatsService
import com.twix.network.service.createUserService
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.dsl.module

internal val apiServiceModule =
    module {
        single<OnboardingService> {
            get<Ktorfit>().createOnboardingService()
        }
        single<AuthService> {
            get<Ktorfit>().createAuthService()
        }
        single<GoalService> {
            get<Ktorfit>().createGoalService()
        }
        single<PhotoLogService> {
            get<Ktorfit>().createPhotoLogService()
        }
        single<StatsService> {
            get<Ktorfit>().createStatsService()
        }
        single<UserService> {
            get<Ktorfit>().createUserService()
        }
    }
