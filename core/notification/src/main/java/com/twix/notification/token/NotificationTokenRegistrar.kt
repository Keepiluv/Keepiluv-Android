package com.twix.notification.token

import co.touchlab.kermit.Logger
import com.google.firebase.messaging.FirebaseMessaging
import com.twix.device_contract.IdProvider
import com.twix.domain.repository.NotificationRepository
import com.twix.result.AppResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class NotificationTokenRegistrar(
    private val notificationRepository: NotificationRepository,
    private val deviceIdProvider: IdProvider,
    private val appScope: CoroutineScope,
) {
    private val logger = Logger.withTag("NotificationTokenRegistrar")

    /**
     * 앱 시작 시 호출
     */
    fun registerCurrentToken() {
        appScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()
                registerInternal(fcmToken)
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                logger.e(e) { "기존 FCM token 등록 실패" }
            }
        }
    }

    /**
     * FirebaseMessagingService.onNewToken 에서 호출
     */
    fun registerFcmToken(fcmToken: String) {
        appScope.launch {
            try {
                registerInternal(fcmToken)
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                logger.e(e) { "새로운 FCM token 등록 실패" }
            }
        }
    }

    fun unregisterCurrentToken() {
        appScope.launch {
            try {
                val fcmToken = FirebaseMessaging.getInstance().token.await()

                when (val result = notificationRepository.deleteFcmToken(fcmToken)) {
                    is AppResult.Success -> logger.d { "FCM token 삭제 성공" }
                    is AppResult.Error -> logger.e { "FCM token 삭제 실패: ${result.error}" }
                }
            } catch (ce: CancellationException) {
                throw ce
            } catch (e: Exception) {
                logger.e(e) { "FCM token 조회/삭제 실패" }
            }
        }
    }

    private suspend fun registerInternal(fcmToken: String) {
        if (fcmToken.isBlank()) {
            logger.d { "FCM token이 비어있음" }
            return
        }

        val deviceId = deviceIdProvider.getOrCreateDeviceId()
        val result =
            notificationRepository.registerFcmToken(
                deviceId = deviceId,
                token = fcmToken,
            )

        when (result) {
            is AppResult.Error -> {
                logger.e { "FCM token 등록 실패: ${result.error}" }
            }
            is AppResult.Success -> {
                logger.d { "FCM token 등록 성공" }
            }
        }
    }
}
