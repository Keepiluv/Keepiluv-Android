package com.twix.analytics

/**
 * 분석 도구에 기록할 이벤트입니다.
 *
 * 파라미터 값으로 [String], [Number], [Boolean]을 지원합니다.
 */
data class AnalyticsEvent(
    val name: String,
    val parameters: Map<String, Any?> = emptyMap(),
)
