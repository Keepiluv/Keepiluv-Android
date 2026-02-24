package com.twix.notification.di

import android.content.Context
import com.twix.navigation_contract.NotificationDeepLinkHandler
import com.twix.navigation_contract.NotificationLaunchEventSource
import com.twix.notification.channel.TwixNotificationChannelManager
import com.twix.notification.deeplink.NotificationDeepLinkParser
import com.twix.notification.routing.NotificationLaunchDispatcher
import com.twix.notification.routing.NotificationRouter
import com.twix.notification.token.NotificationTokenRegistrar
import org.koin.dsl.module

val notificationModule =
    module {
        single { NotificationDeepLinkParser() }
        single { TwixNotificationChannelManager(get<Context>()) }
        single { NotificationTokenRegistrar(get(), get(), get()) }
        single<NotificationDeepLinkHandler> { NotificationRouter(get(), get(), get()) }
        single<NotificationLaunchEventSource> { NotificationLaunchDispatcher() }
    }
