package com.twix.notification.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.notification.Notification
import com.twix.result.AppError
import com.twix.ui.base.LoadableState

@Immutable
data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = true,
    val hasLoadedInitialData: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : LoadableState {
    val showLoading: Boolean
        get() = isLoading && !hasLoadedInitialData

    val showError: Boolean
        get() = error != null && !hasLoadedInitialData

    val canLoadNextPage: Boolean
        get() = hasNext && !isLoading

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
