package com.twix.notification.routing

import co.touchlab.kermit.Logger
import com.twix.domain.repository.NotificationRepository
import com.twix.navigation_contract.AppNavigator
import com.twix.navigation_contract.NotificationDeepLinkHandler
import com.twix.notification.deeplink.NotificationDeepLink
import com.twix.notification.deeplink.NotificationDeepLinkParser
import com.twix.result.AppResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class NotificationRouter(
    private val parser: NotificationDeepLinkParser,
    private val notificationRepository: NotificationRepository,
    private val appScope: CoroutineScope,
) : NotificationDeepLinkHandler {
    private val logger = Logger.withTag("NotificationRouter")

    override fun handle(
        rawDeepLink: String,
        navigator: AppNavigator,
    ) {
        val deepLink = parser.parse(rawDeepLink)
        if (deepLink == null) {
            logger.w("유효하지 않은 deepLink Home으로 이동: $rawDeepLink")
            navigator.toHome() // 기본값
            return
        }

        // 읽음 처리
        markAsReadBestEffort(deepLink.notificationId)

        when (deepLink) {
            is NotificationDeepLink.PartnerConnected -> {
                navigator.toHome()
            }

            is NotificationDeepLink.Poke -> {
                // 내 인증샷
                navigator.toMyPhotolog(goalId = deepLink.goalId, date = deepLink.date)
            }

            is NotificationDeepLink.GoalCompleted -> {
                // 파트너 인증샷
                navigator.toPartnerPhotolog(goalId = deepLink.goalId, date = deepLink.date)
            }

            is NotificationDeepLink.Reaction -> {
                // 내 인증샷
                navigator.toMyPhotolog(goalId = deepLink.goalId, date = deepLink.date)
            }

            is NotificationDeepLink.DailyGoalAchieved -> {
                navigator.toHome()
            }

            is NotificationDeepLink.GoalEnded -> {
                // 통계 화면 종료 탭
                navigator.toStatisticsEndedGoals()
            }

            is NotificationDeepLink.Marketing -> {
                navigator.toHome()
            }
        }
    }

    private fun markAsReadBestEffort(notificationId: Long) {
        appScope.launch {
            val result = notificationRepository.markNotificationAsRead(notificationId)

            when (result) {
                is AppResult.Error -> {
                    logger.e { "알림 읽음 처리 실패: $notificationId, ${result.error}" }
                }
                is AppResult.Success -> {
                    logger.d { "알림 읽음 처리 성공: $notificationId" }
                }
            }
        }
    }
}
