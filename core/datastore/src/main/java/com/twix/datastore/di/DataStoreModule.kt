package com.twix.datastore.di

import android.content.Context
import com.twix.datastore.AuthTokenProvider
import com.twix.datastore.deviceid.DeviceIdProvider
import com.twix.device_contract.IdProvider
import com.twix.token.TokenProvider
import kotlinx.coroutines.CoroutineScope
import org.koin.dsl.module

val dataStoreModule =
    module {
        single<TokenProvider> { AuthTokenProvider(get<Context>(), get<CoroutineScope>()) }
        single<IdProvider> { DeviceIdProvider(get<Context>()) }
    }
