package com.twix.settings

import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.AuthRepository
import com.twix.domain.repository.UserRepository
import com.twix.notification.token.NotificationTokenRegistrar
import com.twix.settings.model.SettingsUiState
import com.twix.ui.base.BaseViewModel

class SettingsViewModel(
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val tokenRegistrar: NotificationTokenRegistrar,
) : BaseViewModel<SettingsUiState, SettingsIntent, SettingsSideEffect>(SettingsUiState()) {
    init {
        fetchUserInfo()
    }

    override suspend fun handleIntent(intent: SettingsIntent) {
        when (intent) {
            SettingsIntent.Retry -> fetchUserInfo()
            is SettingsIntent.SetNickName -> setNickName(intent.nickName)
            SettingsIntent.Logout -> logout()
            SettingsIntent.WithdrawAccount -> withdrawAccount()
        }
    }

    private fun fetchUserInfo() {
        if (currentState.isLoading) return

        launchResult(
            block = { userRepository.fetchUserInfo() },
            onSuccess = {
                reduce {
                    copy(
                        nickName = it.name,
                        email = it.email,
                        hasLoadedContent = true,
                    )
                }
            },
        )
    }

    private fun setNickName(nickName: String) {
        reduce { copy(nickName = nickName) }
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
