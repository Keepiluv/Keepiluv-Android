package com.twix.settings

import com.twix.ui.base.Intent

sealed interface SettingsIntent : Intent {
    data object Retry : SettingsIntent

    data class SetNickName(
        val nickName: String,
    ) : SettingsIntent

    data class SetPokeNotificationEnabled(
        val enabled: Boolean,
    ) : SettingsIntent

    data class SetMarketingNotificationEnabled(
        val enabled: Boolean,
    ) : SettingsIntent

    data class SetNightMarketingNotificationEnabled(
        val enabled: Boolean,
    ) : SettingsIntent

    data object Logout : SettingsIntent

    data object WithdrawAccount : SettingsIntent
}
