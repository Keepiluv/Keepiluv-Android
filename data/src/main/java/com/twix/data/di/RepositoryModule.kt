package com.twix.data.di

import com.twix.data.repository.DefaultAuthRepository
import com.twix.data.repository.DefaultGoalRepository
import com.twix.data.repository.DefaultNotificationRepository
import com.twix.data.repository.DefaultOnboardingRepository
import com.twix.data.repository.DefaultPhotoLogRepository
import com.twix.data.repository.DefaultUserRepository
import com.twix.data.repository.FakeStatsRepository
import com.twix.domain.repository.AuthRepository
import com.twix.domain.repository.GoalRepository
import com.twix.domain.repository.NotificationRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.domain.repository.PhotoLogRepository
import com.twix.domain.repository.StatsRepository
import com.twix.domain.repository.UserRepository
import org.koin.dsl.module

internal val repositoryModule =
    module {
        single<OnBoardingRepository> {
            DefaultOnboardingRepository(get())
        }
        single<GoalRepository> {
            DefaultGoalRepository(get())
        }
        single<AuthRepository> {
            DefaultAuthRepository(get(), get())
        }
        single<PhotoLogRepository> {
            DefaultPhotoLogRepository(get(), get())
        }
        single<UserRepository> {
            DefaultUserRepository(get())
        }
        single<StatsRepository> {
            FakeStatsRepository()
        }
        single<NotificationRepository> {
            DefaultNotificationRepository(get())
        }
    }
