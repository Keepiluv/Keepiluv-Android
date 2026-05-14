package com.twix.notification.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.notification.Notification
import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState

@Immutable
data class NotificationUiState(
    val notificationList: List<Notification> = emptyList(),
    val hasNext: Boolean = true,
    override val hasLoadedContent: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : ContentLoadableState {
    val canLoadNextPage: Boolean
        get() = hasNext && !isLoading

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState = copy(isLoading = isLoading, error = error)
}
