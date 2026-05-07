package com.twix.photolog.detail.component.swipe

/**
 * 카드 스와이프 설정값
 *
 * @property maxCardOffset 카드가 이동할 수 있는 최대 오프셋 거리
 * 이 값을 초과하면 카드 교체가 발생합니다. 기본값: 100dp
 *
 * @property dragVelocityThreshold 드래그 속도 임계값.
 * 이 속도를 초과하면 저항력이 적용되어 드래그가 느려짐
 *
 * @property minimumDragResistance 최소 드래그 저항 비율 (0.0 ~ 1.0).
 * 속도가 임계값을 초과했을 때 적용되는 최소 저항력
 *
 * @property dampingRatio 스프링 애니메이션의 감쇠 비율.
 * 값이 클수록 진동이 빠르게 정지
 *
 * @property stiffness 스프링 애니메이션의 강성.
 * 값이 클수록 애니메이션이 빠르게 완료
 */
data class SwipeCardConfig(
    val maxCardOffset: Int = 100,
    val dragVelocityThreshold: Float = 1200f,
    val minimumDragResistance: Float = 0.35f,
    val dampingRatio: Float = 0.94f,
    val stiffness: Float = 300f,
)
