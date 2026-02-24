package com.twix.designsystem.components.comment

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun CommentBox(
    uiModel: CommentUiModel,
    onCommentChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onHeightMeasured: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier =
            modifier.onSizeChanged { size ->
                onHeightMeasured(size.height.toFloat())
            },
    ) {
        AppText(
            text = if (uiModel.isFocused) stringResource(R.string.comment_condition_guide) else "",
            style = AppTextStyle.B2,
            color = GrayColor.C100,
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommentTextField(
            uiModel = uiModel,
            onCommitComment = onCommentChanged,
            onFocusChanged = onFocusChanged,
        )
    }
}
