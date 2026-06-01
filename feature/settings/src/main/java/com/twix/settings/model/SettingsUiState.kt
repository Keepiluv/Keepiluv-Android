package com.twix.settings.model

import com.twix.result.AppError
import com.twix.ui.base.LoadableState

data class SettingsUiState(
    val nickName: String = "",
    val email: String = "",
    val inviteCode: String = "",
    val pokeNotificationEnabled: Boolean = false,
    val marketingNotificationEnabled: Boolean = false,
    val nightMarketingNotificationEnabled: Boolean = false,
    val notificationSettingsUpdating: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
