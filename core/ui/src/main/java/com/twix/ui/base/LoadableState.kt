package com.twix.ui.base

import com.twix.result.AppError

/**
 * 로딩 상태와 에러를 포함하는 State 인터페이스
 *
 * 로딩/에러 상태가 필요한 화면은 이 인터페이스를 구현하여
 * BaseViewModel의 launchResult가 자동으로 상태를 업데이트하도록 한다.
 *
 */
interface LoadableState : State {
    val isLoading: Boolean
    val error: AppError?

    fun copyLoadableState(
        isLoading: Boolean = this.isLoading,
        error: AppError? = this.error,
    ): LoadableState
}
