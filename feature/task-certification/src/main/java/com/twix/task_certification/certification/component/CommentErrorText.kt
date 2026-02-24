package com.twix.task_certification.certification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun CommentErrorText(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .width(224.dp)
                .height(56.dp)
                .background(color = GrayColor.C400, shape = RoundedCornerShape(12.dp))
                .clip(RoundedCornerShape(12.dp)),
    ) {
        AppText(
            text = stringResource(R.string.comment_error_message),
            style = AppTextStyle.B1,
            color = CommonColor.White,
            modifier = Modifier.align(Alignment.Center),
        )
    }
}

@Preview
@Composable
fun CommentErrorTextPreview() {
    TwixTheme {
        CommentErrorText()
    }
}
