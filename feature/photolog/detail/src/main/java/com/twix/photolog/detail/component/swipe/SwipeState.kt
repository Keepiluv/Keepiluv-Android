package com.twix.photolog.detail.component.swipe

/**
 * 드래그 상태 정보
 */
data class SwipeState(
    val cardOffset: Float = 0f,
    val isCrossingDuringDrag: Boolean = false,
)
