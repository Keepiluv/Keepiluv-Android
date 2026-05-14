package com.twix.ui.base

/**
 * 로딩/에러 상태와 함께 “콘텐츠가 한 번이라도 성공적으로 로드되었는지”를 표현하는 상태 계약.
 *
 * 초기 진입 시에는 전체 화면 로딩/에러를, 이후 재조회 시에는 기존 UI 위 overlay loading을
 * 보여줘야 하는 화면이 구현한다.
 */
interface ContentLoadableState : DefaultLoadableState {
    val hasLoadedContent: Boolean

    /**
     * 초기 화면 진입 단계에서 전체 UI 대신 loading Indicator를 보여줘야 하는지 여부.
     */
    val showLoading: Boolean
        get() = isLoading && !hasLoadedContent

    /**
     * 초기 화면 진입 단계에서 전체 UI 대신 ErrorScreen를 보여줘야 하는지 여부.
     */
    val showError: Boolean
        get() = error != null && !hasLoadedContent

    /**
     * 기존 UI 위에 loading Indicator를 보여줘야 하는지 여부.
     */
    val showOverlayLoading: Boolean
        get() = isLoading && hasLoadedContent
}
