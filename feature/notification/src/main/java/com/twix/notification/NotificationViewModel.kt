package com.twix.notification

import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.NotificationRepository
import com.twix.notification.contract.NotificationIntent
import com.twix.notification.contract.NotificationSideEffect
import com.twix.notification.contract.NotificationUiState
import com.twix.notification.deeplink.NotificationDeepLink
import com.twix.notification.deeplink.NotificationDeepLinkParser
import com.twix.ui.base.BaseViewModel

class NotificationViewModel(
    private val notificationDeepLinkParser: NotificationDeepLinkParser,
    private val notificationRepository: NotificationRepository,
) : BaseViewModel<NotificationUiState, NotificationIntent, NotificationSideEffect>(
        NotificationUiState(),
    ) {
    init {
        fetchNotificationList()
    }

    override suspend fun handleIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.FetchNextPage -> fetchNotificationList()
            is NotificationIntent.NotificationClicked -> openNotification(intent.notificationId)
        }
    }

    private fun fetchNotificationList() {
        if (!currentState.hasNext) return

        val lastId =
            uiState.value.notificationList
                .lastOrNull()
                ?.id

        launchResult(
            block = { notificationRepository.fetchNotifications(lastId = lastId) },
            onSuccess = {
                reduce {
                    markAllNotificationAsRead()
                    copy(notificationList = currentState.notificationList + it.notifications, hasNext = it.hasNext)
                }
            },
            onError = { emitSideEffect(NotificationSideEffect.ShowToast(R.string.toast_fetch_notification_failed, ToastType.ERROR)) },
        )
    }

    private fun markAllNotificationAsRead() {
        launchResult(
            block = { notificationRepository.markAllNotificationsAsRead() },
            onSuccess = {},
        )
    }

    private suspend fun openNotification(id: Long) {
        val notification = uiState.value.notificationList.find { it.id == id } ?: return
        val intent = notificationDeepLinkParser.parse(notification.deepLink)

        when (intent) {
            is NotificationDeepLink.DailyGoalAchieved -> emitSideEffect(NotificationSideEffect.NavigateToHome)
            is NotificationDeepLink.GoalCompleted ->
                emitSideEffect(
                    NotificationSideEffect.NavigateToPartnerPhotolog(intent.goalId, intent.date),
                )
            is NotificationDeepLink.GoalEnded -> emitSideEffect(NotificationSideEffect.NavigateToStatisticsEndedGoals)
            is NotificationDeepLink.Marketing -> emitSideEffect(NotificationSideEffect.NavigateToHome)
            is NotificationDeepLink.PartnerConnected -> emitSideEffect(NotificationSideEffect.NavigateToHome)
            is NotificationDeepLink.Poke -> emitSideEffect(NotificationSideEffect.NavigateToMyPhotolog(intent.goalId, intent.date))
            is NotificationDeepLink.Reaction -> emitSideEffect(NotificationSideEffect.NavigateToMyPhotolog(intent.goalId, intent.date))
            null -> emitSideEffect(NotificationSideEffect.NavigateToHome)
        }
    }
}
