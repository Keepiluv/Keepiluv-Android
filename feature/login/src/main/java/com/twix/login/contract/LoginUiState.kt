package com.twix.login.contract

import com.twix.ui.base.State

data class LoginUiState(
    val isLoggedIn: Boolean = false,
) : State
