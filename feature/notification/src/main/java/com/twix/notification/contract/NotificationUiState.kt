package com.twix.notification.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.notification.Notification
import com.twix.ui.base.State

@Immutable
data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = true,
    val isLoading: Boolean = false, // 알림 리스트를 중복으로 조회하지 않도록 방어 목적의 플래그 변수
) : State
