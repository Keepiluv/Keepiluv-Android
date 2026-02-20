package com.twix.task_certification.editor.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import com.twix.designsystem.R as DesR

@Composable
internal fun TaskCertificationEditorTopBar(
    title: String,
    onBack: () -> Unit,
    onClickSave: () -> Unit,
) {
    CommonTopBar(
        title = title,
        left = {
            Image(
                painter = painterResource(DesR.drawable.ic_arrow3_left),
                contentDescription = "back",
                modifier =
                    Modifier
                        .padding(18.dp)
                        .size(24.dp)
                        .noRippleClickable(onClick = onBack),
            )
        },
        right = {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(GrayColor.C100)
                        .noRippleClickable { onClickSave() },
                contentAlignment = Alignment.Center,
            ) {
                AppText(
                    text = stringResource(DesR.string.word_save),
                    style = AppTextStyle.T2,
                    color = GrayColor.C500,
                )
            }
        },
        modifier = Modifier.background(color = CommonColor.White),
    )
}

@Preview
@Composable
fun TaskCertificationEditorTopBarPreview() {
    TwixTheme {
        Column {
            TaskCertificationEditorTopBar(
                title = "목표 인증",
                onBack = {},
                onClickSave = {},
            )

            TaskCertificationEditorTopBar(
                title = "목표 인증",
                onBack = {},
                onClickSave = {},
            )
        }
    }
}
