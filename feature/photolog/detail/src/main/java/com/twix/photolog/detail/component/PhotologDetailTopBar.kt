package com.twix.photolog.detail.component

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
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun PhotologDetailTopBar(
    title: String,
    canModify: Boolean,
    onBack: () -> Unit,
    onClickModify: () -> Unit,
) {
    CommonTopBar(
        title = title,
        left = {
            Image(
                painter = painterResource(R.drawable.ic_arrow3_left),
                contentDescription = "back",
                modifier =
                    Modifier
                        .padding(18.dp)
                        .size(24.dp)
                        .noRippleClickable(onClick = onBack),
            )
        },
        right = {
            if (canModify) {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(GrayColor.C050)
                            .noRippleClickable { onClickModify() },
                    contentAlignment = Alignment.Center,
                ) {
                    AppText(
                        text = stringResource(R.string.word_modify),
                        style = AppTextStyle.T2,
                        color = GrayColor.C500,
                    )
                }
            }
        },
        modifier = Modifier.background(color = CommonColor.White),
    )
}

@Preview
@Composable
fun PhotologDetailTopBarPreview() {
    TwixTheme {
        Column {
            PhotologDetailTopBar(
                title = "목표 인증",
                canModify = true,
                onBack = {},
                onClickModify = {},
            )

            PhotologDetailTopBar(
                title = "목표 인증",
                canModify = false,
                onBack = {},
                onClickModify = {},
            )
        }
    }
}
