package com.twix.designsystem.components.photolog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppRoundButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun BackgroundCard(
    isCertificated: Boolean,
    uploadedAt: String,
    buttonTitle: String,
    onClick: () -> Unit,
    rotation: Float,
) {
    Column {
        PhotologCard(
            background = GrayColor.C200,
            borderColor = GrayColor.C500,
            rotation = rotation,
        )
        if (isCertificated) {
            AppText(
                text = uploadedAt,
                style = AppTextStyle.B4,
                color = GrayColor.C500,
                modifier =
                    Modifier
                        .padding(end = 36.dp, top = 14.dp)
                        .align(Alignment.End),
            )
        } else {
            Box(Modifier.fillMaxWidth()) {
                Column(
                    Modifier.align(Alignment.TopCenter),
                ) {
                    Spacer(Modifier.height(105.dp))
                    AppRoundButton(
                        modifier =
                            Modifier
                                .width(150.dp)
                                .height(74.dp)
                                .noRippleClickable { onClick() },
                        text = buttonTitle,
                        textColor = GrayColor.C500,
                        backgroundColor = CommonColor.White,
                    )
                }

                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_keepi_sting),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .padding(end = 24.dp, top = 15.dp)
                            .align(Alignment.TopEnd),
                )
            }
        }
    }
}

@Preview
@Composable
fun PreviewBackgroundCard() {
    TwixTheme {
        BackgroundCard(
            buttonTitle = stringResource(R.string.word_sting),
            uploadedAt = "2023.10.31 23:59",
            onClick = {},
            isCertificated = true,
            rotation = -8f,
        )
    }
}
