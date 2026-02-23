package com.yapp.twix

import android.app.Application
import com.kakao.sdk.common.KakaoSdk
import com.yapp.twix.di.initKoin

class TwixApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin(
            context = this,
        )
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)
    }
}
