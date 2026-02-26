package com.twix.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import com.twix.result.AppError
import com.twix.result.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
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

    init {
        // Intent 순차 처리
        viewModelScope.launch {
            intentChannel.receiveAsFlow().collect { intent ->
                try {
                    handleIntent(intent)
                } catch (t: Throwable) {
                    if (t is CancellationException) throw t
                    handleError(t)
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
     * */
    protected fun <D> launchResult(
        onStart: (() -> Unit)? = null, // 비동기 시작 전 처리해야 할 로직 ex) 로딩
        onFinally: (() -> Unit)? = null, // 비동기 종료 후 리소스 정리
        onSuccess: (D) -> Unit, // 비동기 메서드 호출이 성공했을 때 처리해야 할 로직
        onError: (suspend (AppError) -> Unit)? = null, // 비동기 메서드 호출에 실패했을 때 처리해야 할 로직
        block: suspend () -> AppResult<D>, // 비동기 메서드 ex) 서버 통신 메서드
    ): Job =
        viewModelScope.launch {
            try {
                onStart?.invoke()

                when (val result = block()) {
                    is AppResult.Success -> onSuccess(result.data)
                    is AppResult.Error -> {
                        // 공통 처리: 로깅
                        handleError(result.error)
                        // 메서드별 처리: 특정 화면만의 UX ex) 다이얼로그/토스트
                        onError?.invoke(result.error)
                    }
                }
            } catch (e: CancellationException) {
                // 코루틴 취소는 에러로 취급하지 않기
                throw e
            } finally {
                onFinally?.invoke()
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
