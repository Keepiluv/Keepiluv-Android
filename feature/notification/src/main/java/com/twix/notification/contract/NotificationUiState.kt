package com.twix.notification.contract

import com.twix.domain.model.notification.Notification
import com.twix.ui.base.State

data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = false,
) : State
