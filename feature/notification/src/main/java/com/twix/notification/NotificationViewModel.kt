package com.twix.notification

import com.twix.notification.contract.NotificationIntent
import com.twix.notification.contract.NotificationSideEffect
import com.twix.notification.contract.NotificationUiState
import com.twix.ui.base.BaseViewModel

class NotificationViewModel :
    BaseViewModel<NotificationUiState, NotificationIntent, NotificationSideEffect>(
        NotificationUiState(),
    ) {
    override suspend fun handleIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.FetchNextPage -> fetchNotificationList()
            is NotificationIntent.NotificationClicked -> openNotification(intent.notificationId)
        }
    }

    private fun fetchNotificationList() {
        // TODO: NotificationRepository로 조회
    }

    private suspend fun openNotification(id: Long) {
        val notification = uiState.value.notificationList.find { it.id == id } ?: return
        // TODO: NotificationDeepLinkParser로 파싱
    }
}
