package com.twix.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun AppRoundButton(
    contentHeight: Dp,
    contentColor: Color,
    contentBorderColor: Color,
    contentBorderWidth: Dp,
    shadowHeight: Dp,
    shadowOffset: Dp,
    modifier: Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(999.dp)

    Box(modifier = modifier) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(shadowHeight)
                    .offset(y = shadowOffset)
                    .background(color = contentBorderColor, shape = shape),
        )

        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(contentHeight)
                    .background(color = contentColor, shape = shape)
                    .border(
                        color = contentBorderColor,
                        shape = shape,
                        width = contentBorderWidth,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WhiteAppRoundButtonPreview() {
    TwixTheme {
        AppRoundButton(
            modifier =
                Modifier
                    .width(150.dp)
                    .height(74.dp),
            contentColor = CommonColor.White,
            contentHeight = 68.dp,
            contentBorderColor = GrayColor.C500,
            contentBorderWidth = 1.6.dp,
            shadowHeight = 70.dp,
            shadowOffset = 4.dp,
        ) {
            AppText(
                style = AppTextStyle.T2,
                color = GrayColor.C500,
                text = "버튼 이름",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PokeAppRoundButtonPreview() {
    TwixTheme {
        AppRoundButton(
            modifier =
                Modifier
                    .width(64.dp)
                    .height(32.dp),
            contentColor = CommonColor.White,
            contentHeight = 28.dp,
            contentBorderColor = GrayColor.C500,
            contentBorderWidth = 1.dp,
            shadowHeight = 31.dp,
            shadowOffset = 1.dp,
        ) {
            AppText(
                style = AppTextStyle.C2,
                color = GrayColor.C500,
                text = "찌르기!",
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun BlackAppRoundButtonPreview() {
    TwixTheme {
        AppRoundButton(
            modifier =
                Modifier
                    .width(150.dp)
                    .height(74.dp),
            contentColor = GrayColor.C500,
            contentHeight = 68.dp,
            contentBorderColor = CommonColor.White,
            contentBorderWidth = 1.6.dp,
            shadowHeight = 70.dp,
            shadowOffset = 4.dp,
        ) {
            AppText(
                style = AppTextStyle.T2,
                color = CommonColor.White,
                text = "버튼 이름",
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LongAppRoundButtonPreview() {
    TwixTheme {
        AppRoundButton(
            modifier =
                Modifier
                    .width(330.dp)
                    .height(70.dp),
            contentColor = CommonColor.White,
            contentHeight = 68.dp,
            contentBorderColor = GrayColor.C500,
            contentBorderWidth = 1.6.dp,
            shadowHeight = 70.dp,
            shadowOffset = 4.dp,
        ) {
            AppText(
                style = AppTextStyle.T2,
                color = GrayColor.C500,
                text = stringResource(R.string.photolog_editor_retake),
            )
        }
    }
}
