package com.twix.notification.contract

import com.twix.domain.model.goal.Notification
import com.twix.ui.base.State

data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
): State
