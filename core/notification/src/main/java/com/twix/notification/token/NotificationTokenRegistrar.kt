package com.twix.notification.token

import co.touchlab.kermit.Logger
import com.google.firebase.messaging.FirebaseMessaging
import com.twix.device_contract.IdProvider
import com.twix.domain.model.user.User
import com.twix.domain.repository.UserRepository
import com.twix.result.AppResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

class NotificationTokenRegistrar(
//    private val notificationRepository: NotificationRepository,
    private val userRepository: UserRepository,
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

    private suspend fun registerInternal(fcmToken: String) {
        val userResult = userRepository.fetchUserInfo()
        val deviceId = deviceIdProvider.getOrCreateDeviceId()

        when (userResult) {
            is AppResult.Error -> {
                logger.w { "FCM token 등록 스킵 - 사용자 정보 조회 실패: ${userResult.error}" }
                return
            }
            is AppResult.Success<User> -> {
//                notificationRepository.registerFcmToken(
//                    userId = user.data.id,
//                    deviceId = deviceId,
//                    fcmToken = fcmToken,
//                )

                logger.i("FCM token registered. userId=${userResult.data.id}, deviceId=$deviceId")
            }
        }
    }
}
