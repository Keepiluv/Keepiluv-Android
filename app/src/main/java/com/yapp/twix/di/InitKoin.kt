package com.yapp.twix.di

import android.content.Context
import com.twix.data.di.dataModule
import com.twix.datastore.di.dataStoreModule
import com.twix.network.di.networkModule
import com.twix.ui.di.imageModule
import com.twix.util.di.utilModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module

fun initKoin(
    context: Context? = null,
    extraModules: List<Module> = emptyList(),
) {
    startKoin {
        context?.let { androidContext(it) }

        modules(
            buildList {
                addAll(extraModules)
                addAll(featureModules)
                addAll(networkModule)
                addAll(dataModule)
                add(uiModule)
                add(dataStoreModule)
                add(appModule)
                add(utilModule)
                add(imageModule)
            },
        )
    }
}
