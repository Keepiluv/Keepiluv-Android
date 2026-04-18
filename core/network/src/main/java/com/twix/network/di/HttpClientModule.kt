package com.twix.network.di

import com.twix.network.BuildConfig
import com.twix.network.HttpClientProvider
import com.twix.network.UploadHttpClientProvider
import com.twix.network.upload.PresignedUploader
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val httpClientModule =
    module {

        single<HttpClient> {
            HttpClientProvider.createHttpClient(
                tokenProvider = get(),
                authService = lazy { get() },
                baseUrl = BuildConfig.BASE_URL,
                isDebug = BuildConfig.DEBUG,
            )
        }

        single<Ktorfit> {
            Ktorfit
                .Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .httpClient(get<HttpClient>())
                .build()
        }

        single<HttpClient>(named("uploadClient")) { UploadHttpClientProvider.create() }

        single { PresignedUploader(get(named("uploadClient"))) }
    }
