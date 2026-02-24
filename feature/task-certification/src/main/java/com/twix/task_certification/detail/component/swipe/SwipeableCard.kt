package com.twix.task_certification.detail.component.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

/**
 * 드래그하여 스와이프할 수 있는 카드 컴포넌트.
 *
 * ## 동작
 * 1. 사용자가 카드를 드래그
 * 2. threshold 이상 이동 시 → 카드 dismiss
 * 3. 화면 밖으로 날아간 뒤 onSwipe 호출
 * 4. 반대편에서 다시 등장 (spring 복귀)
 *
 * ## 커스터마이징
 * 모든 애니메이션/거리 값은 [SwipeCardSpec] 으로 조절 가능
 */
@Composable
fun SwipeableCard(
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    spec: SwipeCardSpec = SwipeCardSpec(),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    /**
     * 카드 상태 값
     */
    val offsetX = remember { Animatable(0f) }
    val offsetY = remember { Animatable(0f) }
    val opacity = remember { Animatable(1f) }

    /**
     * 드래그 거리 기반 회전
     */
    val rotation =
        (offsetX.value / spec.rotationFactor)
            .coerceIn(-spec.maxRotation, spec.maxRotation)

    Box(
        modifier =
            modifier
                /**
                 * 카드 위치 이동
                 */
                .offset {
                    IntOffset(
                        offsetX.value.roundToInt(),
                        offsetY.value.roundToInt(),
                    )
                }
                /**
                 * 회전 + 투명도 적용
                 */
                .graphicsLayer {
                    rotationZ = rotation
                    alpha = opacity.value
                }.pointerInput(Unit) {
                    detectDragGestures(
                        /**
                         * 드래그 중
                         * → 위치 즉시 반영 (snap)
                         */
                        onDrag = { change, dragAmount ->
                            change.consume()

                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                offsetY.snapTo(offsetY.value + dragAmount.y)
                            }
                        },
                        /**
                         * 드래그 종료 시 처리
                         */
                        onDragEnd = {
                            val thresholdPx = with(density) { spec.dismissThresholdDp.toPx() }

                            val isHorizontal = abs(offsetX.value) > abs(offsetY.value)

                            val shouldDismiss =
                                if (isHorizontal) {
                                    abs(offsetX.value) > thresholdPx
                                } else {
                                    abs(offsetY.value) > thresholdPx
                                }

                            if (shouldDismiss) {
                                coroutineScope.launch {
                                    /**
                                     * 화면 밖으로 날리기
                                     */
                                    val targetX =
                                        if (isHorizontal) {
                                            if (offsetX.value > 0) {
                                                spec.dismissDistancePx
                                            } else {
                                                -spec.dismissDistancePx
                                            }
                                        } else {
                                            0f
                                        }

                                    val targetY =
                                        if (!isHorizontal) {
                                            if (offsetY.value > 0) {
                                                spec.dismissDistancePx
                                            } else {
                                                -spec.dismissDistancePx
                                            }
                                        } else {
                                            0f
                                        }

                                    /**
                                     * dismiss 애니메이션 완료 대기
                                     */
                                    coroutineScope {
                                        launch { offsetX.animateTo(targetX, tween(spec.dismissDuration)) }
                                        launch { offsetY.animateTo(targetY, tween(spec.dismissDuration)) }
                                        launch { opacity.animateTo(0f, tween(spec.dismissDuration)) }
                                    }

                                    /**
                                     * 데이터 교체
                                     */
                                    onSwipe()

                                    /**
                                     * 반대편 위치 세팅
                                     */
                                    offsetX.snapTo(-targetX * spec.reappearOffsetRatio)
                                    offsetY.snapTo(-targetY * spec.reappearOffsetRatio)

                                    /**
                                     * 스프링 복귀
                                     */
                                    launch {
                                        offsetX.animateTo(
                                            0f,
                                            spring(
                                                dampingRatio = spec.springDamping,
                                                stiffness = spec.springStiffness,
                                            ),
                                        )
                                    }
                                    launch {
                                        offsetY.animateTo(
                                            0f,
                                            spring(
                                                dampingRatio = spec.springDamping,
                                                stiffness = spec.springStiffness,
                                            ),
                                        )
                                    }
                                    launch {
                                        opacity.animateTo(1f, spring(spec.springDamping))
                                    }
                                }
                            } else {
                                // threshold 미만 → 제자리 복귀
                                coroutineScope.launch {
                                    launch { offsetX.animateTo(0f, spring()) }
                                    launch { offsetY.animateTo(0f, spring()) }
                                }
                            }
                        },
                    )
                },
    ) {
        content()
    }
}
