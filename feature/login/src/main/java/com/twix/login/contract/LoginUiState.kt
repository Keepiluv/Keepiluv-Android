package com.twix.login.contract

import com.twix.result.AppError
import com.twix.ui.base.DefaultLoadableState

data class LoginUiState(
    val isLoggedIn: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : DefaultLoadableState {
    val showLoading: Boolean
        get() = isLoading

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): DefaultLoadableState = copy(isLoading = isLoading, error = error)
}
