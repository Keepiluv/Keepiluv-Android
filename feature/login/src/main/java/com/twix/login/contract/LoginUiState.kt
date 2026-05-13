package com.twix.login.contract

import com.twix.result.AppError
import com.twix.ui.base.LoadableState

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : LoadableState {
    val showLoading: Boolean
        get() = isLoading

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState = copy(isLoading = isLoading, error = error)
}
