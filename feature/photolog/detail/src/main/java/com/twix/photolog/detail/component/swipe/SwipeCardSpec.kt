package com.twix.photolog.detail.component.swipe

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * SwipeableCard 애니메이션/동작 설정 값 묶음.
 */
data class SwipeCardSpec(
    /** dismiss 판정 거리(dp) */
    val dismissThreshold: Dp = 80.dp,
    /** dismiss 애니메이션 시간(ms) */
    val dismissDuration: Int = 150,
    /** 회전 계산 비율 */
    val rotationFactor: Float = 28f,
    /** 최대 회전 각도 */
    val maxRotation: Float = 16f,
    /** 복귀 시 위치 비율 */
    val reappearOffsetRatio: Float = 0.2f,
)
