package com.twix.notification.contract

import com.twix.ui.base.Intent

sealed interface NotificationIntent: Intent {
    data object NextPage: NotificationIntent
    data class NotificationClicked(
        val notificationId: Long,
    ): NotificationIntent
}
