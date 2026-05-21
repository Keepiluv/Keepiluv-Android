package com.twix.onboarding

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.invitecode.InviteCode
import com.twix.domain.repository.NotificationRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.onboarding.contract.OnBoardingIntent
import com.twix.onboarding.contract.OnBoardingSideEffect
import com.twix.onboarding.contract.OnBoardingUiState
import com.twix.result.AppError
import com.twix.result.AppResult
import com.twix.ui.base.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.time.LocalDate

class OnBoardingViewModel(
    private val onBoardingRepository: OnBoardingRepository,
    private val notificationRepository: NotificationRepository,
) : BaseViewModel<OnBoardingUiState, OnBoardingIntent, OnBoardingSideEffect>(OnBoardingUiState()) {
    private var onboardingStatusJob: Job? = null
    private var connectCoupleJob: Job? = null

    init {
        fetchMyInviteCode()
    }

    private fun fetchMyInviteCode() {
        launchResult(
            block = { onBoardingRepository.fetchInviteCode() },
            onSuccess = { fetchedInviteCode ->
                reduce {
                    copy(
                        inviteCode =
                            inviteCode.copy(
                                myInviteCode = fetchedInviteCode.value,
                            ),
                    )
                }
            },
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
                emitSideEffect(OnBoardingSideEffect.InviteCode.CopyInviteCode(currentState.inviteCode.myInviteCode))
            OnBoardingIntent.ShareInviteLink ->
                emitSideEffect(OnBoardingSideEffect.InviteCode.ShareInviteLink(currentState.inviteCode.myInviteCode))

            // 초대 코드 화면
            OnBoardingIntent.StartPollingStatus -> startPolling()
            OnBoardingIntent.StopPollingStatus -> stopPolling()

            // 프로필 설정 화면
            is OnBoardingIntent.WriteNickName -> reduceNickName(intent.value)
            OnBoardingIntent.SubmitNickName -> handleSubmitNickname()

            // 디데이 설정 화면
            is OnBoardingIntent.SelectDate -> reduceDday(intent.value)
            OnBoardingIntent.SubmitDday -> anniversarySetup()
            OnBoardingIntent.StartDdayPollingStatus -> startDdayPolling()
            OnBoardingIntent.StopDdayPollingStatus -> stopPolling()

            is OnBoardingIntent.SubmitMarketingConsent ->
                initNotificationSettings(
                    intent.isPushEnabled,
                    intent.isMarketingEnabled,
                    intent.isNightMarketingEnabled,
                )
        }
    }

    private fun startPolling() {
        startStatusPolling { status ->
            if (status == OnboardingStatus.COUPLE_CONNECTION) return@startStatusPolling false

            emitSideEffect(OnBoardingSideEffect.CoupleConnection.NavigateToNext)
            true
        }
    }

    private fun startDdayPolling() {
        startStatusPolling { status ->
            if (status != OnboardingStatus.COMPLETED) return@startStatusPolling false

            showAnniversaryAlreadyRegisteredToast()
            emitSideEffect(OnBoardingSideEffect.DdaySetting.NavigateToHome)
            true
        }
    }

    private fun startStatusPolling(onStatusFetched: suspend (OnboardingStatus) -> Boolean) {
        if (onboardingStatusJob?.isActive == true) return
        onboardingStatusJob =
            viewModelScope.launch {
                var consecutiveFailureCount = 0

                while (isActive) {
                    delay(POLLING_INTERVAL_MS)
                    when (val result = onBoardingRepository.fetchOnboardingStatus()) {
                        is AppResult.Success -> {
                            consecutiveFailureCount = 0
                            if (onStatusFetched(result.data)) {
                                onboardingStatusJob = null
                                break
                            }
                        }

                        is AppResult.Error -> {
                            if (++consecutiveFailureCount >= MAX_POLLING_FAILURE_COUNT) {
                                onboardingStatusJob = null
                                break
                            }
                        }
                    }
                }
            }
    }

    private fun stopPolling() {
        onboardingStatusJob?.cancel()
        onboardingStatusJob = null
    }

    private fun reduceInviteCode(value: String) {
        val isValidInviteCode = InviteCode.create(value).isSuccess

        reduce {
            copy(
                inviteCode =
                    inviteCode.copy(
                        partnerInviteCode = value,
                        isValid = isValidInviteCode,
                    ),
            )
        }
        if (isValidInviteCode) connectCouple()
    }

    private fun connectCouple() {
        val currentUiState = currentState.inviteCode
        if (!currentState.inviteCode.isValid) return
        if (connectCoupleJob?.isActive == true) return

        connectCoupleJob =
            launchResult(
                block = { onBoardingRepository.coupleConnection(currentUiState.partnerInviteCode) },
                onSuccess = {
                    stopPolling()
                    tryEmitSideEffect(OnBoardingSideEffect.InviteCode.NavigateToNext)
                },
                onError = { error -> handleCoupleConnectException(error) },
            )
    }

    private suspend fun handleCoupleConnectException(error: AppError) {
        when {
            error is AppError.Http && error.status == 400 && error.code == SELF_INVITE_CODE_ERROR_CODE -> {
                /** 자신의 초대 코드를 입력한 경우 */
                showToast(R.string.toast_self_invite_code, ToastType.ERROR)
            }
            error is AppError.Http && error.status == 404 -> {
                when (error.message) {
                    INVALID_INVITE_CODE_MESSAGE -> {
                        /** 초대 코드를 잘못 입력한 경우 */
                        showToast(R.string.toast_invalid_invite_code, ToastType.ERROR)
                    }
                    ALREADY_USED_INVITE_CODE_MESSAGE -> {
                        /** 상대방이 이미 연결한 경우 */
                        stopPolling()
                        emitSideEffect(OnBoardingSideEffect.InviteCode.NavigateToNext)
                    }
                    else -> showToast(R.string.onboarding_couple_connection_fail, ToastType.ERROR)
                }
            }
            else -> showToast(R.string.onboarding_couple_connection_fail, ToastType.ERROR)
        }
    }

    private fun reduceNickName(value: String) {
        reduce { copy(profile = profile.updateNickname(value)) }
    }

    private suspend fun handleSubmitNickname() {
        if (currentState.isValidNickName) {
            profileSetup()
        } else {
            showToast(R.string.onboarding_profile_invalid_name_length_toast, ToastType.DEFAULT)
        }
    }

    private fun profileSetup() {
        launchResult(
            block = { onBoardingRepository.profileSetup(currentState.profile.nickname) },
            onSuccess = { fetchOnboardingStatus() },
            onError = { showToast(R.string.onboarding_profile_setup_fail, ToastType.ERROR) },
        )
    }

    private fun fetchOnboardingStatus() {
        launchResult(
            block = { onBoardingRepository.fetchOnboardingStatus() },
            onSuccess = { onboardingStatus ->
                when (onboardingStatus) {
                    OnboardingStatus.ANNIVERSARY_SETUP ->
                        tryEmitSideEffect(OnBoardingSideEffect.ProfileSetting.NavigateToNext)

                    OnboardingStatus.COMPLETED ->
                        onProfileAnniversaryAlreadyRegistered()

                    else -> return@launchResult
                }
            },
        )
    }

    private fun onProfileAnniversaryAlreadyRegistered() {
        tryEmitSideEffect(
            OnBoardingSideEffect.ShowToast(
                message = R.string.onboarding_anniversary_already_registered_toast,
                type = ToastType.DEFAULT,
            ),
        )
        tryEmitSideEffect(OnBoardingSideEffect.ProfileSetting.NavigateToHome)
    }

    private fun reduceDday(value: LocalDate) {
        reduce {
            copy(
                dDay = dDay.updateAnniversaryDate(value),
            )
        }
    }

    private fun anniversarySetup() {
        launchResult(
            block = { onBoardingRepository.anniversarySetup(currentState.dDay.anniversaryDate.toString()) },
            onSuccess = { tryEmitSideEffect(OnBoardingSideEffect.DdaySetting.NavigateToHome) },
            onError = {
                showToast(R.string.onboarding_dday_setup_fail, ToastType.ERROR)
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

    private suspend fun showAnniversaryAlreadyRegisteredToast() {
        showToast(R.string.onboarding_anniversary_already_registered_toast, ToastType.DEFAULT)
    }

    companion object {
        private const val ALREADY_USED_INVITE_CODE_MESSAGE = "이미 사용된 초대 코드입니다."
        private const val INVALID_INVITE_CODE_MESSAGE = "유효하지 않은 초대 코드입니다."
        private const val POLLING_INTERVAL_MS = 3_000L
        private const val MAX_POLLING_FAILURE_COUNT = 5
        private const val SELF_INVITE_CODE_ERROR_CODE = "G4000"
    }
}
