package com.twix.notification.di

import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.notification.NotificationViewModel
import com.twix.notification.navigation.NotificationNavGraph
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val notificationModule =
    module {
        single<NavGraphContributor>(named(NavRoutes.NotificationGraph.route)) { NotificationNavGraph }
        viewModelOf(::NotificationViewModel)
    }
