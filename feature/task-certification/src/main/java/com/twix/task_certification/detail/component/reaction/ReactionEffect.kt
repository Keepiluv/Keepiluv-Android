package com.twix.task_certification.detail.component.reaction

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import kotlin.ranges.random

/**
 * 리액션 파티클 애니메이션을 표시하는 컴포저블.
 */
@Composable
fun ReactionEffect(
    targetReaction: ReactionUiModel?,
    modifier: Modifier = Modifier,
    spec: ReactionEffectSpec = ReactionEffectSpec(),
    onFinished: () -> Unit = {},
) {
    if (targetReaction == null) return

    /**
     * 현재 화면에 렌더링 중인 파티클 리스트
     */
    val particles = remember { mutableStateListOf<ReactionParticle>() }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val maxWidthPx = with(density) { maxWidth.toPx() }
        val maxHeightPx = with(density) { maxHeight.toPx() }

        val sidePaddingPx = with(density) { spec.sidePaddingDp.dp.toPx() }
        val startOffsetPx = with(density) { spec.startOffsetDp.dp.toPx() }

        LaunchedEffect(targetReaction) {
            // 1.️ 파티클 생성
            val newParticles =
                List(spec.particleCount) {
                    ReactionParticle(iconRes = targetReaction.imageResources)
                }

            particles.addAll(newParticles)

            var remaining = newParticles.size

            // 2. 파티클 애니메이션 실행
            newParticles.forEach { particle ->
                scope.launch {
                    /**
                     * 살짝 시간차를 둬 자연스럽게 퍼지도록 함
                     * */
                    delay(Random.nextLong(0, spec.staggerDelayMax))

                    /**
                     * 시작 위치 계산
                     * */
                    val minX = sidePaddingPx.toInt()
                    val maxX = (maxWidthPx - sidePaddingPx).toInt()

                    /**
                     * 화면 하단 랜덤 위치
                     * */
                    val startX =
                        if (maxX > minX) (minX..maxX).random().toFloat() else minX.toFloat()

                    val startY = maxHeightPx - startOffsetPx

                    /**
                     * 위로 상승할 거리
                     * */
                    val travelDistance =
                        spec.travelDistanceRange.random().toFloat()

                    /**
                     * 좌우 랜덤 퍼짐
                     * */
                    val spreadY = startY - travelDistance
                    val spreadX = startX + (-spec.spreadX..spec.spreadX).random()

                    /**
                     * 애니메이션 지속 시간
                     * */
                    val duration = spec.durationRange.random()

                    /**
                     * 초기 위치 세팅
                     * */
                    particle.animX.snapTo(startX)
                    particle.animY.snapTo(startY)

                    /**
                     * scale: 커졌다가 사라짐
                     * */
                    launch {
                        particle.animScale.animateTo(1.4f, tween(200))
                        particle.animScale.animateTo(
                            0f,
                            tween(duration - 200, delayMillis = 200),
                        )
                    }

                    /**
                     * Alpha: 페이드 인 → 아웃
                     * */
                    launch {
                        particle.animAlpha.animateTo(1f, tween(100))
                        particle.animAlpha.animateTo(
                            0f,
                            tween(duration, easing = FastOutLinearInEasing),
                        )
                    }

                    /**
                     * x축 이동
                     * */
                    launch {
                        particle.animX.animateTo(
                            spreadX,
                            tween(duration, easing = LinearOutSlowInEasing),
                        )
                    }

                    /**
                     * y축 이동
                     * */
                    particle.animY.animateTo(
                        spreadY,
                        tween(duration, easing = LinearOutSlowInEasing),
                    )

                    particles.remove(particle)

                    remaining -= 1
                    if (remaining == 0) onFinished()
                }
            }
        }

        /**
         * 현재 존재하는 모든 파티클을 화면에 그림
         */
        particles.forEach { particle ->
            /**
             * 파티클마다 랜덤 크기 부여
             */
            key(particle) {
                val randomSize = remember { spec.sizeRangeDp.random().dp }

                Image(
                    painter = painterResource(id = particle.iconRes),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(randomSize)
                            /**
                             * 위치 이동
                             */
                            .offset {
                                IntOffset(
                                    x = particle.animX.value.toInt(),
                                    y = particle.animY.value.toInt(),
                                )
                            }
                            /**
                             * 스케일 / 투명도 / 회전 효과
                             */
                            .graphicsLayer {
                                scaleX = particle.animScale.value
                                scaleY = particle.animScale.value
                                alpha = particle.animAlpha.value
                                rotationZ = (particle.animX.value % 50f) - 25f
                            },
                )
            }
        }
    }
}
