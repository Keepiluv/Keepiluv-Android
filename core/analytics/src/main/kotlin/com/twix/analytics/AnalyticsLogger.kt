package com.twix.analytics

/** 앱에서 사용하는 분석 도구의 공통 인터페이스입니다 */
interface AnalyticsLogger {
    fun log(event: AnalyticsEvent)

    fun setUserId(userId: String?)

    fun setUserProperty(
        name: String,
        value: String?,
    )
}
