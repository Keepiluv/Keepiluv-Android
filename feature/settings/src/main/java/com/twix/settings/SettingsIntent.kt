package com.twix.settings

import com.twix.ui.base.Intent

sealed interface SettingsIntent : Intent {
    data object Retry : SettingsIntent

    data class SetNickName(
        val nickName: String,
    ) : SettingsIntent

    data object Logout : SettingsIntent

    data object WithdrawAccount : SettingsIntent
}
