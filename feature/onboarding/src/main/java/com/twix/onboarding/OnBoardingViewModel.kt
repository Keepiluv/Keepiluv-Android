package com.twix.onboarding

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.repository.NotificationRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.onboarding.model.OnBoardingIntent
import com.twix.onboarding.model.OnBoardingSideEffect
import com.twix.onboarding.model.OnBoardingUiState
import com.twix.result.AppError
import com.twix.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate

class OnBoardingViewModel(
    private val onBoardingRepository: OnBoardingRepository,
    private val notificationRepository: NotificationRepository,
) : BaseViewModel<OnBoardingUiState, OnBoardingIntent, OnBoardingSideEffect>(OnBoardingUiState()) {
    init {
        fetchMyInviteCode()
    }

    private fun fetchMyInviteCode() {
        launchResult(
            block = { onBoardingRepository.fetchInviteCode() },
            onSuccess = { reduce { updateMyInviteCode(it.value) } },
            onError = {
                showToast(R.string.onboarding_couple_fetch_my_invite_code_fail, ToastType.ERROR)
            },
        )
    }

    override suspend fun handleIntent(intent: OnBoardingIntent) {
        when (intent) {
            // 커플 연결 화면
            is OnBoardingIntent.WriteInviteCode -> reduceInviteCode(intent.value)
            OnBoardingIntent.ConnectCouple -> connectCouple()
            OnBoardingIntent.CopyInviteCode ->
                showToast(R.string.toast_invite_code_copy, ToastType.SUCCESS)

            // 프로필 설정 화면
            is OnBoardingIntent.WriteNickName -> reduceNickName(intent.value)
            OnBoardingIntent.SubmitNickName -> handleSubmitNickname()

            // 디데이 설정 화면
            is OnBoardingIntent.SelectDate -> reduceDday(intent.value)
            OnBoardingIntent.SubmitDday -> anniversarySetup()

            is OnBoardingIntent.SubmitMarketingConsent ->
                initNotificationSettings(
                    intent.isPushEnabled,
                    intent.isMarketingEnabled,
                    intent.isNightMarketingEnabled,
                )
        }
    }

    private fun reduceInviteCode(value: String) {
        reduce { updatePartnerInviteCode(value) }
    }

    private fun connectCouple() {
        val currentUiState = currentState.inviteCode
        if (!currentUiState.isValid) return

        launchResult(
            block = { onBoardingRepository.coupleConnection(currentUiState.partnerInviteCode) },
            onSuccess = {
                viewModelScope.launch {
                    emitSideEffect(OnBoardingSideEffect.InviteCode.NavigateToNext)
                }
            },
            onError = { error -> handleCoupleConnectException(error) },
        )
    }

    private suspend fun handleCoupleConnectException(error: AppError) {
        if (error is AppError.Http && error.status == 404) {
            /**
             * 초대 코드를 잘못 입력한 경우
             * */
            if (error.message == INVALID_INVITE_CODE_MESSAGE) {
                showToast(R.string.toast_invalid_invite_code, ToastType.ERROR)
            } else if (error.message == ALREADY_USED_INVITE_CODE_MESSAGE) {
                /**
                 * 상대방이 이미 연결한 경우
                 * */
                emitSideEffect(OnBoardingSideEffect.InviteCode.NavigateToNext)
            } else {
                showToast(R.string.onboarding_couple_connection_fail, ToastType.ERROR)
            }
        }
    }

    private fun reduceNickName(value: String) {
        reduce { updateNickName(value) }
    }

    private fun handleSubmitNickname() {
        if (currentState.isValidNickName) {
            profileSetup()
        } else {
            viewModelScope.launch {
                emitSideEffect(OnBoardingSideEffect.ProfileSetting.ShowInvalidNickNameToast)
            }
        }
    }

    private fun profileSetup() {
        launchResult(
            block = { onBoardingRepository.profileSetup(currentState.profile.nickname) },
            onSuccess = { fetchOnboardingStatus() },
            onError = { emitSideEffect(OnBoardingSideEffect.ProfileSetting.ShowProfileSetupFailToast) },
        )
    }

    private fun fetchOnboardingStatus() {
        launchResult(
            block = { onBoardingRepository.fetchOnboardingStatus() },
            onSuccess = { onboardingStatus ->
                viewModelScope.launch {
                    val sideEffect =
                        when (onboardingStatus) {
                            OnboardingStatus.ANNIVERSARY_SETUP ->
                                OnBoardingSideEffect.ProfileSetting.NavigateToDDaySetting

                            OnboardingStatus.COMPLETED ->
                                OnBoardingSideEffect.ProfileSetting.NavigateToHome

                            else -> return@launch
                        }
                    emitSideEffect(sideEffect)
                }
            },
            onError = {
                // 에러처리 추가
            },
        )
    }

    private fun reduceDday(value: LocalDate) {
        reduce { updateDday(value) }
    }

    private fun anniversarySetup() {
        launchResult(
            block = { onBoardingRepository.anniversarySetup(currentState.dDay.anniversaryDate.toString()) },
            onSuccess = {
                viewModelScope.launch { emitSideEffect(OnBoardingSideEffect.DdaySetting.NavigateToHome) }
            },
            onError = {
                emitSideEffect(OnBoardingSideEffect.DdaySetting.ShowAnniversarySetupFailToast)
            },
        )
    }

    private fun initNotificationSettings(
        isPushEnabled: Boolean,
        isMarketingEnabled: Boolean,
        isNightMarketingEnabled: Boolean,
    ) {
        launchResult(
            block = {
                notificationRepository.initNotificationSettings(
                    isPushEnabled,
                    isMarketingEnabled,
                    isNightMarketingEnabled,
                )
            },
            onSuccess = {},
        )
    }

    private suspend fun showToast(
        message: Int,
        type: ToastType,
    ) {
        emitSideEffect(OnBoardingSideEffect.ShowToast(message, type))
    }

    companion object {
        private const val ALREADY_USED_INVITE_CODE_MESSAGE = "이미 사용된 초대 코드입니다."
        private const val INVALID_INVITE_CODE_MESSAGE = "유효하지 않은 초대 코드입니다."
    }
}
