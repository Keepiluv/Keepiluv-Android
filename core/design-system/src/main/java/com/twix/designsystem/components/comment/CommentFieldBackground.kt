package com.twix.designsystem.components.comment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme

/**
 * 코멘트 입력 UI의 배경이 되는 연결형 원형 도형을 그리는 컴포저블.
 *
 * 여러 개의 원을 가로 방향으로 배치한 뒤 [PathOperation.Union]으로 합쳐,
 * 하나의 연결된 배경 도형처럼 렌더링한다.
 *
 * @param circleCount 배경을 구성하는 원의 개수.
 * @param circleSize 각 원의 지름이자 전체 배경의 높이.
 * @param circleCenterSpacing 인접한 원 중심점 사이의 거리.
 * @param modifier 외부에서 전달받는 [Modifier].
 */
@Composable
internal fun CommentFieldBackground(
    circleCount: Int,
    circleSize: Dp,
    circleCenterSpacing: Dp,
    modifier: Modifier = Modifier,
) {
    /**
     * Canvas 내부 draw scope는 px 단위를 사용하므로,
     * 디자인에서 정의된 dp 값을 실제 그리기에 사용할 px 값으로 변환한다.
     */
    val density = LocalDensity.current

    /**
     * 피그마 기준 테두리 두께(1.6dp)를 px로 변환한 값.
     */
    val strokeWidth = with(density) { 1.6.dp.toPx() }

    /**
     * 원 중심 간 간격을 px로 변환한 값.
     * 각 원의 x 시작 위치(left)를 계산할 때 사용한다.
     */
    val circleSpacing = with(density) { circleCenterSpacing.toPx() }

    /**
     * 전체 배경 도형의 가로 길이.
     *
     * 첫 번째 원의 전체 너비(circleSize)에
     * 나머지 원들이 중심 간격(circleCenterSpacing)만큼 오른쪽으로 이동하며 배치되므로
     * 아래 공식으로 전체 폭을 계산한다.
     *
     * e.g
     * - circleSize = 64.dp
     * - circleCenterSpacing = 50.dp
     * - circleCount = 5
     * => totalWidth = 64 + 50 * 4 = 264.dp
     */
    val totalWidth = circleSize + circleCenterSpacing * (circleCount - 1)

    Canvas(
        modifier =
            modifier
                .width(totalWidth)
                .height(circleSize),
    ) {
        /**
         * Canvas의 높이를 원의 지름으로 사용한다.
         * 이 컴포저블은 높이 == 원 지름인 정원(circular) 기준으로 동작한다.
         */
        val circleDiameter = size.height

        /**
         * 모든 원을 합쳐 만든 최종 Path.
         *
         * 처음에는 null이고,
         * 원을 하나씩 만들면서 Union 연산으로 계속 누적한다.
         */
        var unionPath: Path? = null

        repeat(circleCount) { index ->
            /**
             * 현재 원의 왼쪽 시작 x 좌표.
             *
             * 원의 중심 간격을 기준으로 각 원을 오른쪽으로 이동시킨다.
             * 0번째 원 -> 0
             * 1번째 원 -> spacing
             * 2번째 원 -> spacing * 2
             * ...
             */
            val left = index * circleSpacing

            /**
             * 현재 순서의 원 하나를 나타내는 Path.
             *
             * addOval(Rect)는 지정된 사각형에 맞는 타원을 추가한다.
             */
            val ovalPath =
                Path().apply {
                    addOval(
                        Rect(
                            left = left,
                            top = 0f,
                            right = left + circleDiameter,
                            bottom = circleDiameter,
                        ),
                    )
                }

            /**
             * 누적된 Path와 현재 원 Path를 합집합(Union)으로 결합한다.
             *
             * - 첫 번째 원이면 unionPath가 없으므로 그대로 사용
             * - 두 번째 원부터는 기존 Path와 새 원을 합쳐 하나의 연결된 도형으로 만든다
             *
             * 이 과정 덕분에 원과 원이 겹치는 내부 경계선은 제거되고,
             * 최종적으로 외곽선만 남는 하나의 shape처럼 다룰 수 있다.
             */
            unionPath =
                if (unionPath == null) {
                    ovalPath
                } else {
                    Path.combine(
                        operation = PathOperation.Union,
                        path1 = unionPath,
                        path2 = ovalPath,
                    )
                }
        }

        /**
         * circleCount가 0인 비정상 케이스를 방어한다.
         * 정상 흐름에서는 null이 아니어야 한다.
         */
        val finalPath = unionPath ?: return@Canvas

        /**
         * 합쳐진 최종 배경 도형 내부를 흰색으로 채운다.
         */
        drawPath(
            path = finalPath,
            color = CommonColor.White,
        )

        /**
         * 최종 Path의 외곽선을 그린다.
         */
        drawPath(
            path = finalPath,
            color = GrayColor.C500,
            style = Stroke(strokeWidth),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CommentFieldBackgroundPreview() {
    TwixTheme {
        CommentFieldBackground(
            circleCount = 5,
            circleSize = 40.dp,
            circleCenterSpacing = 30.dp,
        )
    }
}
