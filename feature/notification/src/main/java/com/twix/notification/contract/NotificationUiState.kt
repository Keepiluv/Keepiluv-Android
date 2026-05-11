package com.twix.notification.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.notification.Notification
import com.twix.result.AppError
import com.twix.ui.base.LoadableState

@Immutable
data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = true,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
