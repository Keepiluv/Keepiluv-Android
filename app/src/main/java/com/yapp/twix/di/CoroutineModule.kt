package com.yapp.twix.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.qualifier.named
import org.koin.dsl.module

/**
 * 전역 코루틴이 필요한 상황
 * · 전역/장수/인프라 객체(fcm 토큰 등록 객체, 알림 딥링크 네비게이션 처리 객체 등)가 독립적인 코루틴을 생성하면 수명 제어가 안됨
 * */
val coroutineModule =
    module {
        single<CoroutineScope>(named("AppScope")) {
            CoroutineScope(SupervisorJob() + Dispatchers.IO)
        }
    }
