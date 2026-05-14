package com.twix.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.twix.result.AppError
import com.twix.result.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

abstract class BaseViewModel<S : State, I : Intent, SE : SideEffect>(
    initialState: S,
) : ViewModel() {
    protected open val logger: Logger =
        Logger.withTag(this::class.simpleName ?: "BaseViewModel")

    // State
    private val stateHolder = StateHolder(initialState)
    val uiState: StateFlow<S> = stateHolder.state
    protected val currentState: S get() = stateHolder.current

    // SideEffect
    private val sideEffectHolder = SideEffectHolder<SE>()
    val sideEffect: Flow<SE> = sideEffectHolder.flow

    // Intent
    private val intentChannel = Channel<I>(Channel.BUFFERED)

    private val loadingCount = MutableStateFlow(0)

    init {
        // Intent 순차 처리
        viewModelScope.launch {
            intentChannel.receiveAsFlow().collect { intent ->
                try {
                    handleIntent(intent)
                } catch (exception: CancellationException) {
                    throw exception
                } catch (throwable: Throwable) {
                    handleError(throwable)
                }
            }
        }
    }

    /**
     * UI에서 Intent를 발생시키는 메서드
     * */
    fun dispatch(intent: I) {
        val result = intentChannel.trySend(intent)
        if (result.isFailure) {
            logger.w { "이벤트 유실: $intent, 원인 = ${result.exceptionOrNull()}" }
        }
    }

    /**
     * State를 변경하는 메서드
     * */
    protected fun reduce(reducer: S.() -> S) {
        stateHolder.reduce(reducer)
    }

    /**
     * SideEffect를 발생시키는 메서드
     * */
    protected suspend fun emitSideEffect(effect: SE) {
        sideEffectHolder.emit(effect)
    }

    protected fun tryEmitSideEffect(effect: SE) {
        sideEffectHolder.tryEmit(effect)
    }

    /**
     * Intent를 처리하는 메서드
     * */
    protected abstract suspend fun handleIntent(intent: I)

    /**
     * 서버 통신 메서드 호출 및 응답을 처리하는 헬퍼 메서드
     *
     * DefaultLoadableState를 구현한 경우 자동으로 isLoading과 error를 업데이트한다.
     * 일반 State를 구현한 경우 onStart/onFinally로 화면별 로딩 상태를 직접 관리해야 한다.
     *
     * ## 에러 처리 가이드라인
     *
     * ### 1. 데이터 로딩 (초기 로드, 화면 진입 시)
     * - DefaultLoadableState 구현 시 자동으로 error 상태 업데이트
     * - `onError = null` 또는 추가 로직만 처리
     * - 사용 예: 화면 진입 시 데이터 fetch, 리스트 초기 로드
     *
     * ### 2. 유저 액션 (버튼 클릭, 폼 제출 등)
     * - `onError`에서 토스트 메시지 표시
     * - 사용 예: 로그인, 목표 생성/수정/삭제, 좋아요, 찌르기
     *
     * ### 3. 백그라운드 새로고침
     * - `onError = null` (조용히 실패)
     * - 에러를 사용자에게 노출하지 않음
     * - 사용 예: EventBus를 통한 자동 새로고침
     *
     * @param onStart 비동기 시작 전 처리해야 할 로직 ex) 화면별 로딩 상태
     * @param onFinally 비동기 종료 후 리소스 정리
     * @param onSuccess 비동기 메서드 호출이 성공했을 때 처리해야 할 로직
     * @param onError 비동기 메서드 호출에 실패했을 때 처리해야 할 로직
     * @param block 비동기 메서드 ex) 서버 통신 메서드
     */
    protected fun <D> launchResult(
        onStart: (() -> Unit)? = null,
        onFinally: (() -> Unit)? = null,
        onSuccess: (D) -> Unit,
        onError: (suspend (AppError) -> Unit)? = null,
        block: suspend () -> AppResult<D>,
    ): Job =
        viewModelScope.launch {
            try {
                // 1. 에러 초기화 및 로딩 시작
                clearError()
                startLoading()
                onStart?.invoke()

                // 2. API 호출 및 결과 처리
                val result = block()
                handleApiResult(result, onSuccess, onError)
            } catch (exception: CancellationException) {
                // 코루틴 취소는 에러로 취급하지 않기
                throw exception
            } finally {
                // 3. 리소스 정리 및 로딩 종료
                onFinally?.invoke()
                stopLoading()
            }
        }

    /**
     * 에러 초기화
     * DefaultLoadableState를 구현한 경우 자동으로 error를 null로 업데이트
     */
    private fun clearError() {
        reduceLoadableState { copyState(error = null) }
    }

    /**
     * 로딩 상태 시작
     * DefaultLoadableState를 구현한 경우 자동으로 isLoading을 true로 업데이트
     */
    private fun startLoading() {
        loadingCount.update { it + 1 }
        if (loadingCount.value == 1) {
            reduceLoadableState { copyState(isLoading = true) }
        }
    }

    /**
     * 로딩 상태 종료
     * DefaultLoadableState를 구현한 경우 자동으로 isLoading을 false로 업데이트
     */
    private fun stopLoading() {
        loadingCount.update { maxOf(0, it - 1) }
        if (loadingCount.value == 0) {
            reduceLoadableState { copyState(isLoading = false) }
        }
    }

    /**
     * 에러 업데이트
     * DefaultLoadableState를 구현한 경우 자동으로 error를 업데이트
     */
    private fun updateError(error: AppError) {
        reduceLoadableState { copyState(error = error) }
    }

    /**
     * API 결과 처리
     */
    private suspend fun <D> handleApiResult(
        result: AppResult<D>,
        onSuccess: (D) -> Unit,
        onError: (suspend (AppError) -> Unit)?,
    ) {
        when (result) {
            is AppResult.Success -> onSuccess(result.data)
            is AppResult.Error -> {
                // 공통 처리: 로깅 및 DefaultLoadableState 에러 업데이트
                handleError(result.error)
                updateError(result.error)
                // 메서드별 처리: 특정 화면만의 UX ex) 다이얼로그/토스트
                onError?.invoke(result.error)
            }
        }
    }

    private inline fun reduceLoadableState(
        crossinline reducer: DefaultLoadableState.() -> DefaultLoadableState,
    ) {
        if (currentState !is DefaultLoadableState) return

        reduce {
            val loadableState = this as? DefaultLoadableState ?: return@reduce this
            @Suppress("UNCHECKED_CAST")
            loadableState.reducer() as S
        }
    }

    /**
     * Throwable용 핸들러 ex) Intent 처리 중 발생한 예외
     * */
    protected open fun handleError(t: Throwable) {
        // TODO: 크래시 리포트
        logger.e(t) { "Unhandled error while handling intent" }
    }

    /**
     * AppError용 핸들러 ex) 서버통신에서 발생한 에러
     * */
    protected open fun handleError(error: AppError) {
        when (error) {
            is AppError.Http ->
                logger.e {
                    "HTTP error: status=${error.status}, code=${error.code}, message=${error.message}"
                }
            is AppError.Network -> logger.e(error.cause) { "Network error" }
            is AppError.Timeout -> logger.e(error.cause) { "Timeout error" }
            is AppError.Serialization -> logger.e(error.cause) { "Serialization error" }
            is AppError.Unknown -> logger.e(error.cause) { "Unknown error" }
            is AppError.Auth.Unauthorized ->
                logger.e {
                    "Unauthorized error: status=${error.status}, code=${error.code}, message=${error.message}"
                }
            is AppError.Auth.TokenExpired ->
                logger.e {
                    "TokenExpired error: status=${error.status}, code=${error.code}, message=${error.message}"
                }
        }
    }

    // 리소스 정리
    override fun onCleared() {
        intentChannel.close()
        super.onCleared()
    }
}
