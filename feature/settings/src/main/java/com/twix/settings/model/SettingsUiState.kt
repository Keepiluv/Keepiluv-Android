package com.twix.settings.model

import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState

data class SettingsUiState(
    val nickName: String = "",
    val email: String = "",
    override val hasLoadedContent: Boolean = false,
    val isAccountActionInFlight: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : ContentLoadableState {
    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState = copy(isLoading = isLoading, error = error)
}
