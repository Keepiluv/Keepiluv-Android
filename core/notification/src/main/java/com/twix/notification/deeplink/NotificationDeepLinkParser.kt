package com.twix.notification.deeplink

import android.net.Uri
import androidx.core.net.toUri
import co.touchlab.kermit.Logger
import java.time.LocalDate

class NotificationDeepLinkParser {
    private val logger = Logger.withTag("NotificationDeepLinkParser")

    fun parse(raw: String?): NotificationDeepLink? {
        if (raw.isNullOrBlank()) return null

        return try {
            val uri = raw.toUri()
            val scheme = uri.scheme
            val host = uri.host
            val action = uri.lastPathSegment

            if (scheme != "twix" || host != "notification" || action.isNullOrBlank()) {
                logger.w("유효하지 않는 deepLink 포맷입니다.: $raw")
                return null
            }

            val notificationId = uri.requireLongQuery("notificationId")

            when (action) {
                "partner-connected" -> NotificationDeepLink.PartnerConnected(notificationId)
                "poke" ->
                    NotificationDeepLink.Poke(
                        notificationId = notificationId,
                        goalId = uri.requireLongQuery("goalId"),
                        date = uri.requireLocalDateQuery("date"),
                    )
                "goal-completed" ->
                    NotificationDeepLink.GoalCompleted(
                        notificationId = notificationId,
                        goalId = uri.requireLongQuery("goalId"),
                        date = uri.requireLocalDateQuery("date"),
                    )
                "reaction" ->
                    NotificationDeepLink.Reaction(
                        notificationId = notificationId,
                        goalId = uri.requireLongQuery("goalId"),
                        date = uri.requireLocalDateQuery("date"),
                    )
                "daily-goal-achieved" -> NotificationDeepLink.DailyGoalAchieved(notificationId)
                "goal-ended" -> NotificationDeepLink.GoalEnded(notificationId)
                "marketing" -> NotificationDeepLink.Marketing(notificationId)
                else -> {
                    logger.w("알 수 없는 notification action: $action")
                    null
                }
            }
        } catch (e: Exception) {
            logger.e("notification deepLink 파싱에 실패했습니다.: $raw")
            null
        }
    }

    private fun Uri.requireLongQuery(name: String): Long =
        getQueryParameter(name)?.toLongOrNull()
            ?: throw IllegalArgumentException("유효하지 않은 Long param입니다.: $name")

    private fun Uri.requireLocalDateQuery(name: String): LocalDate =
        getQueryParameter(name)?.let(LocalDate::parse)
            ?: throw IllegalArgumentException("유효하지 않은 Date param입니다.: $name")
}
