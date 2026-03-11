package com.twix.photolog.editor.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppRoundButton
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun RetakeButton(
    onClickRetake: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AppRoundButton(
        text = stringResource(R.string.photolog_editor_retake),
        textColor = GrayColor.C500,
        backgroundColor = CommonColor.White,
        modifier =
            modifier
                .fillMaxWidth()
                .height(68.dp)
                .padding(horizontal = 30.dp)
                .noRippleClickable { onClickRetake() },
    )
}

@Preview
@Composable
private fun RetakeButtonPreview() {
    TwixTheme {
        RetakeButton(onClickRetake = {})
    }
}
