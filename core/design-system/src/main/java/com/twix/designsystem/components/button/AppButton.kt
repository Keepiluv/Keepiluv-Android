package com.twix.designsystem.components.button

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun AppButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor: Color = CommonColor.White,
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
        modifier = modifier,
    ) {
        AppText(
            text = text,
            color = textColor,
            style = AppTextStyle.T2,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 14.dp),
        )
    }
}
