package com.twix.task_certification.detail.component.swipe

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * SwipeableCard 애니메이션/동작 설정 값 묶음.
 */
data class SwipeCardSpec(
    /** dismiss 판정 거리(dp) */
    val dismissThresholdDp: Dp = 60.dp,
    /** 화면 밖으로 날아가는 거리(px) */
    val dismissDistancePx: Float = 1000f,
    /** dismiss 애니메이션 시간(ms) */
    val dismissDuration: Int = 150,
    /** 회전 계산 비율 */
    val rotationFactor: Float = 28f,
    /** 최대 회전 각도 */
    val maxRotation: Float = 8f,
    /** 복귀 시 위치 비율 */
    val reappearOffsetRatio: Float = 0.2f,
    /** spring damping */
    val springDamping: Float = 0.84f,
    /** spring stiffness */
    val springStiffness: Float = 300f,
)
