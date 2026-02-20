package com.twix.task_certification.detail.component.reaction

/**
 * ReactionEffect 애니메이션 설정 값 묶음.
 */
data class ReactionEffectSpec(
    val particleCount: Int = 15,
    /** 생성 시차(ms) */
    val staggerDelayMax: Long = 200L,
    /** 애니메이션 지속 시간(ms) */
    val durationRange: IntRange = 800..1200,
    /** 상승 거리(px) */
    val travelDistanceRange: IntRange = 500..1500,
    /** 좌우 퍼짐(px) */
    val spreadX: Int = 180,
    /** 아이콘 크기(dp) */
    val sizeRangeDp: IntRange = 32..48,
    /** 시작 위치 패딩(dp) */
    val sidePaddingDp: Int = 40,
    /** 시작 바닥 높이(dp) */
    val startOffsetDp: Int = 80,
)
