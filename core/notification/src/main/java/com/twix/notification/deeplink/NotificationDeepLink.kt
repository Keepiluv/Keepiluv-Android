package com.twix.notification.deeplink

import java.time.LocalDate

sealed interface NotificationDeepLink {
    val notificationId: Long

    data class PartnerConnected(
        override val notificationId: Long,
    ) : NotificationDeepLink

    data class Poke(
        override val notificationId: Long,
        val goalId: Long,
        val date: LocalDate,
    ) : NotificationDeepLink

    data class GoalCompleted(
        override val notificationId: Long,
        val goalId: Long,
        val date: LocalDate,
    ) : NotificationDeepLink

    data class Reaction(
        override val notificationId: Long,
        val goalId: Long,
        val date: LocalDate,
    ) : NotificationDeepLink

    data class DailyGoalAchieved(
        override val notificationId: Long,
    ) : NotificationDeepLink

    data class GoalEnded(
        override val notificationId: Long,
    ) : NotificationDeepLink

    data class Marketing(
        override val notificationId: Long,
    ) : NotificationDeepLink
}
