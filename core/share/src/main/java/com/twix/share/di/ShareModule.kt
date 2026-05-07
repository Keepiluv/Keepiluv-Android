package com.twix.share.di

import com.twix.navigation_contract.InviteLaunchEventSource
import com.twix.share.InviteLaunchDispatcher
import org.koin.dsl.module

val shareModule =
    module {
        single<InviteLaunchEventSource> { InviteLaunchDispatcher() }
    }
