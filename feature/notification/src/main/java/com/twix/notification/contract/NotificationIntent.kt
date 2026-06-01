package com.twix.notification.contract

import com.twix.ui.base.Intent

sealed interface NotificationIntent : Intent {
    data object Retry : NotificationIntent

    data object FetchNextPage : NotificationIntent

    data class NotificationClicked(
        val notificationId: Long,
    ) : NotificationIntent
}
