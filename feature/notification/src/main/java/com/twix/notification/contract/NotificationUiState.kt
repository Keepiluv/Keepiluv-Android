package com.twix.notification.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.notification.Notification
import com.twix.ui.base.State

@Immutable
data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = true,
) : State
