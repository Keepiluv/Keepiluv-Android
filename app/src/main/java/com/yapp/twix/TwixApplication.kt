package com.yapp.twix

import android.app.Application
import co.touchlab.kermit.Logger
import com.kakao.sdk.common.KakaoSdk
import com.twix.notification.token.NotificationTokenRegistrar
import com.yapp.twix.di.initKoin
import org.koin.java.KoinJavaComponent.getKoin

class TwixApplication : Application() {
    private val logger = Logger.withTag("TwixApplication")

    override fun onCreate() {
        super.onCreate()

        initKoin(
            context = this,
        )
        KakaoSdk.init(this, BuildConfig.KAKAO_NATIVE_APP_KEY)

        try {
            getKoin().get<NotificationTokenRegistrar>().registerCurrentToken()
        } catch (e: Exception) {
            logger.e(e) { "FCM token 등록 실패" }
        }
    }
}
