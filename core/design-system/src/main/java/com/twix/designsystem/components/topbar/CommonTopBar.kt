package com.twix.designsystem.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun CommonTopBar(
    title: String,
    left: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    right: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
    ) {
        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(60.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            left()

            VerticalDivider(thickness = 1.dp, color = GrayColor.C500)

            AppText(
                text = title,
                style = AppTextStyle.H4Brand,
                color = GrayColor.C500,
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .weight(1f),
            )

            VerticalDivider(thickness = 1.dp, color = GrayColor.C500)

            Box(
                modifier =
                    Modifier
                        .size(60.dp),
            ) {
                right?.invoke()
            }
        }

        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)
    }
}

@Preview(showBackground = true)
@Composable
fun CommonTopBarPreview() {
    TwixTheme {
        CommonTopBar(
            title = "Title",
            left = { Box(modifier = Modifier.size(60.dp)) },
            right = { Box(modifier = Modifier.size(60.dp)) },
        )
    }
}
