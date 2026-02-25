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
        fetchInitialNotificationList()
    }

    override suspend fun handleIntent(intent: NotificationIntent) {
        when (intent) {
            NotificationIntent.FetchNextPage -> fetchNextNotificationList()
            is NotificationIntent.NotificationClicked -> handleNotificationClick(intent.notificationId)
        }
    }

    private fun fetchInitialNotificationList() {
        if (currentState.isLoading) return

        launchResult(
            onStart = { reduce { copy(isLoading = true) } },
            onFinally = { reduce { copy(isLoading = false) } },
            block = { notificationRepository.fetchNotifications() },
            onSuccess = {
                markAllNotificationAsRead()
                reduce { copy(notificationList = it.notifications, hasNext = it.hasNext) }
            },
        )
    }

    private fun fetchNextNotificationList() {
        if (!currentState.hasNext || currentState.isLoading) return

        val lastId =
            currentState.notificationList
                .lastOrNull()
                ?.id

        launchResult(
            onStart = { reduce { copy(isLoading = true) } },
            onFinally = { reduce { copy(isLoading = false) } },
            block = { notificationRepository.fetchNotifications(lastId = lastId) },
            onSuccess = {
                reduce {
                    copy(
                        notificationList =
                            (currentState.notificationList + it.notifications).distinctBy { n ->
                                n.id
                            },
                        hasNext = it.hasNext,
                    )
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

    private suspend fun handleNotificationClick(id: Long) {
        val notification = uiState.value.notificationList.find { it.id == id } ?: return
        val intent = notificationDeepLinkParser.parse(notification.deepLink)

        markNotificationAsRead(id)

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

    // 알림 읽음 처리는 best effort가 정책이므로 에러 처리는 생략
    private fun markNotificationAsRead(id: Long) {
        launchResult(
            block = { notificationRepository.markNotificationAsRead(id) },
            onSuccess = {},
        )
    }
}
