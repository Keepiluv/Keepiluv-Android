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
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.designsystem.theme.DimmedColor
import com.twix.ui.extension.noRippleClickable

/**
 * 특정 UI 요소(Anchor) 하단에 [CommentBox]를 배치하고, 키보드 활성화 상태에 따라 위치를 동적으로 조정하는 프레임 컴포저블
 *
 * 이 컴포저블은 평상시에는 [anchorBottom] 좌표를 기준으로 배치
 * 키보드가 올라와 코맨트창이 가려질 경우 키보드 바로 위로 위치를 자동으로 이동
 *
 * @param uiModel 댓글창의 상태(텍스트, 포커스 상태)를 담고 있는 데이터 모델
 * @param anchorBottom 댓글창 배치의 기준이 되는 상위 요소의 바닥(Bottom) Y 좌표 (px 단위)
 * @param onCommentChanged 댓글 내용이 변경될 때 호출되는 콜백
 * @param onFocusChanged 댓글창의 포커스 상태가 변경될 때 호출되는 콜백 (포커스 시 배경 딤 처리 등에 사용)
 * @param modifier 레이아웃 수정을 위한 [Modifier]
 */
@Composable
fun CommentAnchorFrame(
    uiModel: CommentUiModel,
    anchorBottom: Float,
    onCommentChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (anchorBottom == 0f) return

    val density = LocalDensity.current
    val focusManager = LocalFocusManager.current
    val imeBottom = WindowInsets.ime.getBottom(density)
    val paddingBottom = with(density) { 24.dp.toPx() }

    var commentBoxHeight by remember { mutableFloatStateOf(0f) }
    val defaultY = anchorBottom - commentBoxHeight - paddingBottom

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenHeight = constraints.maxHeight.toFloat()
        val keyboardTop = screenHeight - imeBottom

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
        CommentBox(
            uiModel = uiModel,
            onCommentChanged = onCommentChanged,
            onFocusChanged = onFocusChanged,
            onHeightMeasured = { height ->
                if (commentBoxHeight != height) commentBoxHeight = height
            },
            modifier =
                Modifier
                    .fillMaxWidth()
                    .offset {
                        /**
                         * 키보드가 활성화되었고, 댓글창이 키보드 위치보다 아래에 있어 가려지는 경우
                         * */
                        val targetY =
                            if (imeBottom > 0 && (defaultY + commentBoxHeight) > keyboardTop) {
                                /**
                                 * 화면 전체 높이 - 키보드 높이 - 코멘트 높이
                                 * */
                                (screenHeight - imeBottom - commentBoxHeight).toInt()
                            } else {
                                /**
                                 * 키보드가 없거나, 댓글창이 키보드에 가려지지 않는 경우
                                 * */
                                defaultY.toInt()
                            }
                        IntOffset(x = 0, y = targetY)
                    },
        )
    }
}
