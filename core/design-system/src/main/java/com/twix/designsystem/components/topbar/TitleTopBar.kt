package com.twix.designsystem.components.topbar

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
fun TitleTopBar(
    title: String,
    right: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(80.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Spacer(Modifier.width(20.dp))

        AppText(
            modifier = Modifier.weight(1f),
            text = title,
            style = AppTextStyle.H3Brand,
            color = GrayColor.C500,
            textAlign = TextAlign.Start,
        )

        if (right != null) {
            Box(
                modifier =
                    Modifier
                        .size(44.dp)
                        .padding(end = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                right()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TitleTopBarPreview() {
    TwixTheme {
        TitleTopBar(title = "스탬프 통계")
    }
}
