package com.twix.settings.model

import com.twix.ui.base.State

data class SettingsUiState(
    val nickName: String = "",
    val email: String = "",
) : State
