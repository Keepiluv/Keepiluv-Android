package com.twix.settings

import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.AuthRepository
import com.twix.domain.repository.NotificationRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.domain.repository.UserRepository
import com.twix.notification.token.NotificationTokenRegistrar
import com.twix.settings.model.SettingsUiState
import com.twix.ui.base.BaseViewModel

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val tokenRegistrar: NotificationTokenRegistrar,
    private val onBoardingRepository: OnBoardingRepository,
    private val notificationRepository: NotificationRepository,
) : BaseViewModel<SettingsUiState, SettingsIntent, SettingsSideEffect>(SettingsUiState()) {
    init {
        fetchUserInfo()
        fetchNotificationSetting()
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Retry -> {
                fetchUserInfo()
                fetchNotificationSetting()
            }
            is SettingsIntent.SetNickName -> setNickName(intent.nickName)
            is SettingsIntent.SetPokeNotificationEnabled -> {
                setPokeNotificationEnabled(intent.enabled)
            }

            is SettingsIntent.SetMarketingNotificationEnabled -> {
                setMarketingNotificationEnabled(intent.enabled)
            }

            is SettingsIntent.SetNightMarketingNotificationEnabled -> {
                setNightMarketingNotificationEnabled(intent.enabled)
            }
            SettingsIntent.Logout -> logout()
            SettingsIntent.WithdrawAccount -> withdrawAccount()
        }
    }

    private fun fetchNotificationSetting() {
        launchResult(
            block = { notificationRepository.fetchNotificationSettings() },
            onSuccess = { setting ->
                reduceNotificationSettings(
                    isPushEnabled = setting.isPushEnabled,
                    isMarketingPushEnabled = setting.isMarketingPushEnabled,
                    isNightPushEnabled = setting.isNightPushEnabled,
                )
                reduce { copy(isLoadedNotificationSettings = true) }
            },
            onError = {
                tryEmitSideEffect(
                    SettingsSideEffect.ShowToast(
                        R.string.toast_notification_setting_load_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun fetchUserInfo() {
        launchResult(
            block = { userRepository.fetchUserInfo() },
            onSuccess = {
                reduce {
                    copy(
                        nickName = it.name,
                        email = it.email,
                        inviteCode = it.inviteCode,
                        isLoadedUserInfo = true,
                    )
                }
            },
        )
    }

    private fun setNickName(nickName: String) {
        val originalNickName = currentState.nickName
        reduce { copy(nickName = nickName) }

        launchResult(
            block = { onBoardingRepository.updateProfile(nickName) },
            onSuccess = {},
            onError = { reduce { copy(nickName = originalNickName) } },
        )
    }

    private fun setPokeNotificationEnabled(enabled: Boolean) {
        if (currentState.notificationSettingsUpdating) return

        val originalState = currentState

        reduce {
            copy(
                pokeNotificationEnabled = enabled,
                notificationSettingsUpdating = true,
            )
        }

        launchResult(
            onFinally = { reduce { copy(notificationSettingsUpdating = false) } },
            block = { notificationRepository.updatePokeNotificationSetting(enabled) },
            onSuccess = { setting ->
                reduceNotificationSettings(
                    isPushEnabled = setting.isPushEnabled,
                    isMarketingPushEnabled = setting.isMarketingPushEnabled,
                    isNightPushEnabled = setting.isNightPushEnabled,
                )
            },
            onError = {
                restoreNotificationSettings(originalState)
                tryEmitSideEffect(
                    SettingsSideEffect.ShowToast(
                        R.string.toast_notification_setting_update_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun setMarketingNotificationEnabled(enabled: Boolean) {
        if (currentState.notificationSettingsUpdating) return

        val originalState = currentState

        reduce {
            copy(
                marketingNotificationEnabled = enabled,
                notificationSettingsUpdating = true,
            )
        }

        launchResult(
            onFinally = { reduce { copy(notificationSettingsUpdating = false) } },
            block = { notificationRepository.updateMarketingNotificationSetting(enabled) },
            onSuccess = { setting ->
                reduceNotificationSettings(
                    isPushEnabled = setting.isPushEnabled,
                    isMarketingPushEnabled = setting.isMarketingPushEnabled,
                    isNightPushEnabled = setting.isNightPushEnabled,
                )
            },
            onError = {
                restoreNotificationSettings(originalState)
                tryEmitSideEffect(
                    SettingsSideEffect.ShowToast(
                        R.string.toast_notification_setting_update_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun setNightMarketingNotificationEnabled(enabled: Boolean) {
        if (currentState.notificationSettingsUpdating) return

        val originalState = currentState

        reduce {
            copy(
                nightMarketingNotificationEnabled = enabled,
                notificationSettingsUpdating = true,
            )
        }

        launchResult(
            onFinally = { reduce { copy(notificationSettingsUpdating = false) } },
            block = { notificationRepository.updateNightNotificationSetting(enabled) },
            onSuccess = { setting ->
                reduceNotificationSettings(
                    isPushEnabled = setting.isPushEnabled,
                    isMarketingPushEnabled = setting.isMarketingPushEnabled,
                    isNightPushEnabled = setting.isNightPushEnabled,
                )
            },
            onError = {
                restoreNotificationSettings(originalState)
                tryEmitSideEffect(
                    SettingsSideEffect.ShowToast(
                        R.string.toast_notification_setting_update_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun reduceNotificationSettings(
        isPushEnabled: Boolean,
        isMarketingPushEnabled: Boolean,
        isNightPushEnabled: Boolean,
    ) {
        reduce {
            copy(
                pokeNotificationEnabled = isPushEnabled,
                marketingNotificationEnabled = isMarketingPushEnabled,
                nightMarketingNotificationEnabled = isNightPushEnabled,
            )
        }
    }

    private fun restoreNotificationSettings(state: SettingsUiState) {
        reduceNotificationSettings(
            isPushEnabled = state.pokeNotificationEnabled,
            isMarketingPushEnabled = state.marketingNotificationEnabled,
            isNightPushEnabled = state.nightMarketingNotificationEnabled,
        )
    }

    private fun logout() {
        if (currentState.isAccountActionInFlight) return

        reduce { copy(isAccountActionInFlight = true) }

        launchResult(
            block = { authRepository.logout() },
            onFinally = { reduce { copy(isAccountActionInFlight = false) } },
            onSuccess = {
                tokenRegistrar.unregisterCurrentToken()
                tryEmitSideEffect(SettingsSideEffect.ShowToast(R.string.toast_logout_completed, ToastType.SUCCESS))
                tryEmitSideEffect(SettingsSideEffect.NavigateToLogin)
            },
            onError = { tryEmitSideEffect(SettingsSideEffect.ShowToast(R.string.toast_logout_failed, ToastType.ERROR)) },
        )
    }

    private fun withdrawAccount() {
        if (currentState.isAccountActionInFlight) return

        reduce { copy(isAccountActionInFlight = true) }

        launchResult(
            block = { authRepository.withdrawAccount() },
            onFinally = { reduce { copy(isAccountActionInFlight = false) } },
            onSuccess = {
                tryEmitSideEffect(SettingsSideEffect.ShowToast(R.string.toast_account_deleted, ToastType.SUCCESS))
                tryEmitSideEffect(SettingsSideEffect.NavigateToLogin)
            },
            onError = { tryEmitSideEffect(SettingsSideEffect.ShowToast(R.string.toast_account_delete_failed, ToastType.ERROR)) },
        )
    }
}
