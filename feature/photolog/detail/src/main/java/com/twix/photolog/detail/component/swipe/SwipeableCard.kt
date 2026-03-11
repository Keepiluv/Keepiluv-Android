package com.twix.photolog.detail.component.swipe

import androidx.compose.animation.core.Animatable
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
    isDisplayingMyPhoto: Boolean,
    modifier: Modifier = Modifier,
    spec: SwipeCardSpec = SwipeCardSpec(),
    content: @Composable () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    val threshold = with(density) { spec.dismissThreshold.toPx() }

    /**
     * 카드 상태 값
     */
    val offsetX = remember { Animatable(0f) }
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
                        0,
                    )
                }
                /**
                 * 회전 + 투명도 적용
                 */
                .graphicsLayer {
                    rotationZ = rotation
                    alpha = opacity.value
                }.pointerInput(isDisplayingMyPhoto) {
                    detectDragGestures(
                        /**
                         * 드래그 중
                         * → 위치 즉시 반영 (snap)
                         */
                        onDrag = { _, dragAmount ->
                            coroutineScope.launch {
                                offsetX.snapTo(offsetX.value + dragAmount.x)
                            }
                        },
                        /**
                         * 드래그 종료 시 처리
                         */
                        onDragEnd = {
                            val shouldDismiss = abs(offsetX.value) > threshold
                            coroutineScope.launch {
                                if (shouldDismiss) {
                                    onSwipe()

                                    /**
                                     * 반대편 위치 세팅
                                     */
                                    val reappearStartX =
                                        if (isDisplayingMyPhoto) {
                                            spec.dismissDistance * spec.reappearOffsetRatio
                                        } else {
                                            -spec.dismissDistance * spec.reappearOffsetRatio
                                        }
                                    offsetX.snapTo(reappearStartX)

                                    launch { offsetX.animateTo(0f) }
                                    launch { opacity.animateTo(1f) }
                                } else {
                                    // threshold 미만 → 제자리 복귀
                                    launch { offsetX.animateTo(0f) }
                                }
                            }
                        },
                    )
                },
    ) {
        content()
    }
}
