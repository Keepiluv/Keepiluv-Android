package com.twix.photolog.detail.component.swipe

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.min

@Composable
fun SwipeableCard(
    isShowMyCard: Boolean,
    onSwipe: () -> Unit,
    modifier: Modifier = Modifier,
    config: SwipeCardConfig = SwipeCardConfig(),
    content: @Composable (SwipeState) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    /**
     * 카드 상태 값
     */
    val cardOffset = remember { Animatable(0f) }
    var isCrossingDuringDrag by remember { mutableStateOf(false) }

    val maxCardOffset = with(density) { config.maxCardOffset.dp.toPx() }
    val dragVelocityThreshold = config.dragVelocityThreshold
    val minimumDragResistance = config.minimumDragResistance

    /**
     * 드래그 폭 계산
     */
    fun calculateResistedDragWidth(
        proposedWidth: Float,
        velocity: Float,
    ): Float {
        val speed = abs(velocity)
        if (speed <= dragVelocityThreshold) return proposedWidth

        val excessSpeedRatio = (speed - dragVelocityThreshold) / dragVelocityThreshold
        val normalizedOverflow = min(excessSpeedRatio, 1f)
        val resistance = 1 - (normalizedOverflow * (1 - minimumDragResistance))
        return proposedWidth * resistance
    }

    /**
     * 무한 좌우 스크롤 적용을 위한 반복 오프셋 계산
     */
    fun calculateRepeatedCardOffset(width: Float): Float {
        val direction = if (width >= 0) 1f else -1f
        val fullCycleDistance = maxCardOffset * 2
        val progressInCycle = abs(width) % fullCycleDistance

        if (progressInCycle <= maxCardOffset) return progressInCycle * direction

        val reversedProgress = fullCycleDistance - progressInCycle
        return reversedProgress * direction
    }

    /**
     * 카드 교차 여부 판단
     */
    fun shouldCrossCards(width: Float): Boolean {
        val fullCycleDistance = maxCardOffset * 2
        val progressInCycle = abs(width) % fullCycleDistance
        return progressInCycle > maxCardOffset
    }

    /**
     * 카드 오프셋 애니메이션으로 원위치
     */
    fun animateCardToOrigin() {
        coroutineScope.launch {
            cardOffset.animateTo(
                0f,
                animationSpec =
                    spring(
                        dampingRatio = config.dampingRatio,
                        stiffness = config.stiffness,
                    ),
            )
        }
    }

    /**
     * 드래그 상태 초기화
     */
    fun resetCrossingState() {
        isCrossingDuringDrag = false
    }

    /**
     * 드래그 처리 (오프셋 업데이트 및 교차 상태 갱신)
     */
    fun handleDragMovement(
        totalDragDistance: Float,
        velocity: Float,
    ) {
        val maximumOffsetRange = maxCardOffset * 2
        val resistedWidth = calculateResistedDragWidth(totalDragDistance, velocity)

        if (resistedWidth !in -maximumOffsetRange..maximumOffsetRange) return

        coroutineScope.launch {
            cardOffset.snapTo(calculateRepeatedCardOffset(resistedWidth))
        }
        isCrossingDuringDrag = shouldCrossCards(resistedWidth)
    }

    /**
     * 드래그 종료 처리
     */
    fun finalizeDrag() {
        if (isCrossingDuringDrag) {
            onSwipe()
        }
        animateCardToOrigin()
        resetCrossingState()
    }

    Box(
        modifier =
            modifier
                .pointerInput(isShowMyCard) {
                    var totalDragDistance = 0f
                    var lastVelocity = 0f

                    detectDragGestures(
                        onDrag = { _, dragAmount ->
                            val horizontalDragAmount = dragAmount.x
                            totalDragDistance += horizontalDragAmount
                            lastVelocity = horizontalDragAmount

                            handleDragMovement(totalDragDistance, abs(lastVelocity))
                        },
                        onDragEnd = {
                            finalizeDrag()
                            totalDragDistance = 0f
                            lastVelocity = 0f
                        },
                    )
                },
    ) {
        content(
            SwipeState(
                cardOffset = cardOffset.value,
                isCrossingDuringDrag = isCrossingDuringDrag,
            ),
        )
    }
}
