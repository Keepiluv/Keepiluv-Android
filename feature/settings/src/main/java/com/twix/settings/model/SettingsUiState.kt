package com.twix.settings.model

import com.twix.result.AppError
import com.twix.ui.base.LoadableState

data class SettingsUiState(
    val nickName: String = "",
    val email: String = "",
    val hasLoadedInitialData: Boolean = false,
    val isAccountActionInFlight: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : LoadableState {
    val showLoading: Boolean
        get() = isLoading && !hasLoadedInitialData

    val showError: Boolean
        get() = error != null && !hasLoadedInitialData

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
