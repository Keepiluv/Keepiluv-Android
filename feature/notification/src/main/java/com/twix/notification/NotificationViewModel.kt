package com.twix.notification

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.notification.Notification
import com.twix.domain.repository.NotificationRepository
import com.twix.notification.contract.NotificationIntent
import com.twix.notification.contract.NotificationSideEffect
import com.twix.notification.contract.NotificationUiState
import com.twix.notification.deeplink.NotificationDeepLink
import com.twix.notification.deeplink.NotificationDeepLinkParser
import com.twix.result.AppResult
import com.twix.ui.base.BaseViewModel
import kotlinx.coroutines.launch

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
            NotificationIntent.Retry -> fetchInitialNotificationList()
            NotificationIntent.FetchNextPage -> fetchNextNotificationList()
            is NotificationIntent.NotificationClicked -> handleNotificationClick(intent.notificationId)
        }
    }

    private fun fetchInitialNotificationList() {
        if (currentState.isLoading) return

        launchResult(
            block = { notificationRepository.fetchNotifications() },
            onSuccess = {
                markAllNotificationAsRead()
                reduce {
                    copy(
                        notificationList = it.notifications,
                        hasNext = it.hasNext,
                        hasLoadedContent = true,
                    )
                }
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
            block = { notificationRepository.fetchNotifications(lastId = lastId) },
            onSuccess = {
                reduce {
                    copy(
                        notificationList =
                            (currentState.notificationList + it.notifications).distinctBy { notification ->
                                notification.id
                            },
                        hasNext = it.hasNext,
                    )
                }
            },
            onError = { emitSideEffect(NotificationSideEffect.ShowToast(R.string.toast_fetch_notification_failed, ToastType.ERROR)) },
        )
    }

    private fun markAllNotificationAsRead() {
        viewModelScope.launch {
            when (val result = notificationRepository.markAllNotificationsAsRead()) {
                is AppResult.Success -> Unit
                is AppResult.Error -> handleError(result.error)
            }
        }
    }

    private suspend fun handleNotificationClick(id: Long) {
        val notification = uiState.value.notificationList.find { it.id == id } ?: return
        val intent = notificationDeepLinkParser.parse(notification.deepLink)

        if (!notification.isRead) markNotificationAsRead(notification)

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
    private suspend fun markNotificationAsRead(notification: Notification) {
        reduce {
            copy(
                notificationList =
                    currentState.notificationList.map { item ->
                        if (item.id == notification.id) item.copy(isRead = true) else item
                    },
            )
        }

        when (val result = notificationRepository.markNotificationAsRead(notification.id)) {
            is AppResult.Success -> Unit
            is AppResult.Error -> handleError(result.error)
        }
    }
}
