package com.twix.designsystem.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun AppRoundButton(
    text: String,
    textColor: Color,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    textStyle: AppTextStyle = AppTextStyle.T2,
    borderColor: Color = GrayColor.C500,
    hasBorder: Boolean = true,
) {
    Box(
        modifier = modifier,
    ) {
        if (hasBorder) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .offset(y = 4.dp)
                        .background(
                            color = borderColor,
                            shape = RoundedCornerShape(100),
                        ),
            )
        }

        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(
                        color = backgroundColor,
                        shape = RoundedCornerShape(100),
                    ).then(
                        if (hasBorder) {
                            Modifier.border(
                                width = 1.6.dp,
                                color = borderColor,
                                shape = RoundedCornerShape(100),
                            )
                        } else {
                            Modifier
                        },
                    ),
            contentAlignment = Alignment.Center,
        ) {
            AppText(
                style = textStyle,
                color = textColor,
                text = text,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppRoundButtonPreview() {
    TwixTheme {
        Column {
            AppRoundButton(
                modifier =
                    Modifier
                        .width(330.dp)
                        .height(68.dp),
                text = "버튼임니다",
                textColor = GrayColor.C500,
                backgroundColor = CommonColor.White,
            )
            Spacer(modifier = Modifier.height(10.dp))
            AppRoundButton(
                modifier =
                    Modifier
                        .width(330.dp)
                        .height(68.dp),
                text = "버튼임니다",
                textColor = CommonColor.White,
                backgroundColor = GrayColor.C500,
                hasBorder = false,
            )
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
