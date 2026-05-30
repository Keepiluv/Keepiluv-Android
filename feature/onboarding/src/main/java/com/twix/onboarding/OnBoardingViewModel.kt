package com.twix.onboarding

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.invitecode.InviteCode
import com.twix.domain.repository.NotificationRepository
import com.twix.domain.repository.OnBoardingRepository
import com.twix.onboarding.contract.OnBoardingIntent
import com.twix.onboarding.contract.OnBoardingLoadingAction
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
    private var pollingJob: Job? = null
    private var connectCoupleJob: Job? = null
    private var inviteCodeInitializationJob: Job? = null

    init {
        fetchMyInviteCode()
    }

    private fun fetchMyInviteCode() {
        if (inviteCodeInitializationJob?.isActive == true) return

        inviteCodeInitializationJob =
            launchResult(
                block = { onBoardingRepository.fetchInviteCode() },
                onSuccess = { fetchedInviteCode ->
                    reduce {
                        copy(
                            inviteCode =
                                inviteCode.copy(
                                    myInviteCode = fetchedInviteCode.value,
                                ),
                            hasLoadedContent = true,
                        )
                    }
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
            OnBoardingIntent.RetryFetchInviteCode -> fetchMyInviteCode()

            // 초대 코드 화면
            OnBoardingIntent.StartPollingStatus -> startPolling()
            OnBoardingIntent.StopPollingStatus -> stopPolling()

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

    private fun startPolling() {
        if (pollingJob?.isActive == true) return
        pollingJob =
            viewModelScope.launch {
                /**
                 * 네트워크 오류 등으로 API 호출이 연속으로 실패한 횟수
                 * 성공 응답을 받으면 0으로 리셋되며, MAX_POLLING_FAILURE_COUNT에 도달하면 폴링을 중단한다.
                 * 일시적인 오류에는 폴링을 유지하되, 지속적인 오류 상황에서 무한 루프를 방지하기 위해 사용한다.
                 * **/
                var consecutiveFailureCount = 0

                while (isActive) {
                    delay(POLLING_INTERVAL_MS)
                    when (val result = onBoardingRepository.fetchOnboardingStatus()) {
                        is AppResult.Success -> {
                            consecutiveFailureCount = 0
                            if (result.data != OnboardingStatus.COUPLE_CONNECTION) {
                                stopPolling()
                                emitSideEffect(OnBoardingSideEffect.CoupleConnection.NavigateToNext)
                                break
                            }
                        }

                        is AppResult.Error -> {
                            if (++consecutiveFailureCount >= MAX_POLLING_FAILURE_COUNT) {
                                stopPolling()
                                break
                            }
                        }
                    }
                }
            }
    }

    private fun stopPolling() {
        pollingJob?.cancel()
        pollingJob = null
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
        if (currentState.loadingAction != null) return
        if (connectCoupleJob?.isActive == true) return

        connectCoupleJob =
            launchResult(
                onStart = { startLoadingAction(OnBoardingLoadingAction.CONNECT_COUPLE) },
                onFinally = { clearLoadingAction(OnBoardingLoadingAction.CONNECT_COUPLE) },
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
        if (currentState.loadingAction != null) return

        launchResult(
            onStart = { startLoadingAction(OnBoardingLoadingAction.SUBMIT_PROFILE) },
            onFinally = { clearLoadingAction(OnBoardingLoadingAction.SUBMIT_PROFILE) },
            block = { onBoardingRepository.profileSetup(currentState.profile.nickname) },
            onSuccess = { fetchOnboardingStatus() },
            onError = { showToast(R.string.onboarding_profile_setup_fail, ToastType.ERROR) },
        )
    }

    private fun fetchOnboardingStatus() {
        launchResult(
            block = { onBoardingRepository.fetchOnboardingStatus() },
            onSuccess = { onboardingStatus ->
                val sideEffect =
                    when (onboardingStatus) {
                        OnboardingStatus.ANNIVERSARY_SETUP ->
                            OnBoardingSideEffect.ProfileSetting.NavigateToNext

                        OnboardingStatus.COMPLETED ->
                            OnBoardingSideEffect.ProfileSetting.NavigateToHome

                        else -> return@launchResult
                    }
                tryEmitSideEffect(sideEffect)
            },
        )
    }

    private fun reduceDday(value: LocalDate) {
        reduce {
            copy(
                dDay = dDay.updateAnniversaryDate(value),
            )
        }
    }

    private fun anniversarySetup() {
        if (currentState.loadingAction != null) return

        launchResult(
            onStart = { startLoadingAction(OnBoardingLoadingAction.SUBMIT_DDAY) },
            onFinally = { clearLoadingAction(OnBoardingLoadingAction.SUBMIT_DDAY) },
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
        if (currentState.loadingAction != null) return

        launchResult(
            onStart = { startLoadingAction(OnBoardingLoadingAction.SUBMIT_MARKETING_CONSENT) },
            onFinally = { clearLoadingAction(OnBoardingLoadingAction.SUBMIT_MARKETING_CONSENT) },
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

    private fun startLoadingAction(action: OnBoardingLoadingAction) {
        reduce { copy(loadingAction = action) }
    }

    private fun clearLoadingAction(expectedAction: OnBoardingLoadingAction) {
        reduce {
            if (loadingAction != expectedAction) return@reduce this
            copy(loadingAction = null)
        }
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
        private const val POLLING_INTERVAL_MS = 3_000L
        private const val MAX_POLLING_FAILURE_COUNT = 5
        private const val SELF_INVITE_CODE_ERROR_CODE = "G4000"
    }
}
