package com.twix.designsystem.components.comment

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.DimmedColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

/**
 * 특정 UI 요소(Anchor) 하단에 [CommentTextField]를 배치하고, 키보드 활성화 상태에 따라 위치를 동적으로 조정하는 프레임 컴포저블
 *
 * 이 컴포저블은 평상시에는 [anchorBottom] 좌표를 기준으로 배치
 * 키보드가 올라와 코맨트창이 가려질 경우 키보드 바로 위로 위치를 자동으로 이동
 *
 * @param uiModel 코멘트창의 상태(텍스트, 포커스 상태)를 담고 있는 데이터 모델
 * @param anchorBottom 코멘트창 배치의 기준이 되는 상위 요소의 바닥(Bottom) Y 좌표 (px 단위).
 * @param onCommentChanged 코멘트 내용이 변경될 때 호출되는 콜백
 * @param onFocusChanged 코멘트창의 포커스 상태가 변경될 때 호출되는 콜백 (포커스 시 배경 딤 처리 등에 사용)
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 */
@Composable
fun CommentAnchorFrame(
    uiModel: CommentUiModel,
    anchorBottom: Float,
    paddingBottom: Dp,
    onCommentChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (anchorBottom == 0f) return

    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current

    val imeBottom = WindowInsets.ime.getBottom(density)
    val navBottom = WindowInsets.navigationBars.getBottom(density)

    val commentTextFieldPaddingBottom = with(density) { paddingBottom.toPx() }
    val guideTextPaddingBottom = with(density) { 8.dp.toPx() }

    var commentTextFieldHeight by remember { mutableFloatStateOf(0f) }
    var guideTextHeight by remember { mutableFloatStateOf(0f) }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenHeight = constraints.maxHeight.toFloat()

        AnimatedVisibility(
            visible = uiModel.isFocused,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(DimmedColor.D070)
                        .noRippleClickable { focusManager.clearFocus() },
            )
        }

        val commentTextFieldY =
            calculateCommentFieldY(
                anchorBottom = anchorBottom,
                screenHeight = screenHeight,
                imeBottom = imeBottom,
                navBottom = navBottom,
                textFieldHeight = commentTextFieldHeight,
                paddingBottom = commentTextFieldPaddingBottom,
            )

        if (uiModel.isFocused) {
            AppText(
                text = stringResource(R.string.comment_condition_guide),
                style = AppTextStyle.B2,
                color = GrayColor.C100,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .onSizeChanged { size -> guideTextHeight = size.height.toFloat() }
                        .offset {
                            // 코멘트 입력창 바로 위에서 텍스트 높이와 패딩만큼 올려서 배치
                            val offsetY = commentTextFieldY - guideTextHeight - guideTextPaddingBottom
                            IntOffset(0, offsetY.toInt())
                        },
            )
        }

        CommentTextField(
            uiModel = uiModel,
            onCommitComment = onCommentChanged,
            onFocusChanged = onFocusChanged,
            onHeightMeasured = { height ->
                if (height != commentTextFieldHeight) commentTextFieldHeight = height
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset { IntOffset(x = 0, y = commentTextFieldY) },
        )
    }
}

/**
 * 키보드(IME) 상태에 따라 코멘트 입력창의 최적 Y 좌표(px)를 계산합니다.
 *
 * 키보드가 올라오지 않은 경우에는 Anchor 기준 위치를 반환하고,
 * 키보드가 코멘트창을 조금이라도 가리는 경우에는 키보드 바로 위로 위치를 이동합니다.
 *
 * 모든 파라미터와 반환값은 px 단위입니다.
 *
 * @param anchorBottom 코멘트창이 기준으로 삼는 앵커 요소의 하단 Y 좌표 (px)
 * @param screenHeight 화면 전체 높이 (px). 키보드 상단 좌표 계산에 사용
 * @param imeBottom 키보드(IME) 인셋 높이 (px)
 * @param navBottom 네비게이션 바 인셋 높이 (px)
 * @param textFieldHeight 코멘트 입력창의 실측 높이 (px)
 * @param paddingBottom 코멘트 입력창과 기준점 사이의 여백 (px)
 * @return 코멘트 입력창을 배치할 Y 좌표 (px)
 */
private fun calculateCommentFieldY(
    anchorBottom: Float,
    screenHeight: Float,
    imeBottom: Int,
    navBottom: Int,
    textFieldHeight: Float,
    paddingBottom: Float,
): Int {
    // 1. 키보드가 없을 때의 기본 위치: 앵커 하단에서 코멘트창 높이 + 패딩만큼 위
    val defaultY = anchorBottom - textFieldHeight - paddingBottom

    // 2. 네비게이션 바 인셋을 제외한 키보드 순수 높이
    //    제스처 내비게이션 환경에서는 navBottom이 0이므로 그대로 imeBottom이 사용됨
    //    버튼 내비게이션 환경에서는 navBottom만큼 차감하여 실제 키보드 높이만 반영
    val pureImeHeight = (imeBottom - navBottom).coerceAtLeast(0)

    // 3. 키보드 상단 Y 좌표: 화면 하단에서 키보드 높이만큼 올라간 지점
    val keyboardTop = screenHeight - pureImeHeight

    // 4. 키보드가 활성화된 경우 코멘트창을 키보드 바로 위에 배치할 Y 좌표
    val keyboardTopY = keyboardTop - textFieldHeight - paddingBottom

    // 판정: 키보드가 올라와 있고, 기본 위치의 코멘트창 하단이 키보드 상단을 침범하는 경우
    // 즉 키보드가 조금이라도 코멘트창을 가리면 키보드 위로 즉시 이동
    val isImeVisible = imeBottom > 0 && (defaultY + textFieldHeight + paddingBottom) > keyboardTop

    return if (isImeVisible) {
        keyboardTopY.toInt()
    } else {
        defaultY.toInt()
    }
}
