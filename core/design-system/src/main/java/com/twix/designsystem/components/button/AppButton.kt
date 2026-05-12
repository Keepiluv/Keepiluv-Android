package com.twix.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = CommonColor.White,
    textStyle: AppTextStyle = AppTextStyle.T2,
    backgroundColor: Color = GrayColor.C500,
    enabled: Boolean = true,
    cornerRadius: Dp = 12.dp,
    border: BorderStroke? = null,
    onClick: () -> Unit = {},
) {
    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(cornerRadius),
        border = border,
        onClick = onClick,
        enabled = enabled,
        modifier =
            modifier
                .height(52.dp),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            AppText(
                text = text,
                color = textColor,
                style = textStyle,
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppButtonPreview() {
    TwixTheme {
        AppButton(
            text = "Button",
            onClick = {},
        )
    }
}
