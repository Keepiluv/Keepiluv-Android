package com.twix.ui.base

import com.twix.result.AppError

/**
 * 비동기 요청의 로딩/에러만 공통으로 관리하는 최소 상태 계약.
 *
 * 단순 액션 화면처럼 “기존 콘텐츠 유지 여부”를 별도로 판단할 필요가 없는 경우
 * 이 인터페이스만 구현하면 된다.
 */
interface DefaultLoadableState : State {
    val isLoading: Boolean
    val error: AppError?

    fun copyState(
        isLoading: Boolean = this.isLoading,
        error: AppError? = this.error,
    ): DefaultLoadableState
}
