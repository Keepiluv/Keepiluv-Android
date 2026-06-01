package com.twix.settings.model

import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState

data class SettingsUiState(
    val nickName: String = "",
    val email: String = "",
    val inviteCode: String = "",
    val pokeNotificationEnabled: Boolean = false,
    val marketingNotificationEnabled: Boolean = false,
    val nightMarketingNotificationEnabled: Boolean = false,
    val notificationSettingsUpdating: Boolean = false,
    override val isLoading: Boolean = false,
    val isLoadedUserInfo: Boolean = false,
    val isLoadedNotificationSettings: Boolean = false,
    val isAccountActionInFlight: Boolean = false,
    override val error: AppError? = null,
) : ContentLoadableState {
    override val hasLoadedContent: Boolean
        get() = isLoadedUserInfo && isLoadedNotificationSettings

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState = copy(isLoading = isLoading, error = error)
}
