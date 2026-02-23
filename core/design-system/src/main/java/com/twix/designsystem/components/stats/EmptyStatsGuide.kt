package com.twix.designsystem.components.stats

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun EmptyStatsGuide(
    text: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(124.dp))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_empty_face),
            contentDescription = null,
        )

        Spacer(Modifier.height(10.dp))

        AppText(
            text = text,
            style = AppTextStyle.T2,
            color = GrayColor.C200,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatsGuidePreview() {
    TwixTheme {
        EmptyStatsGuide(text = "아직 목표가 없어요!")
    }
}
