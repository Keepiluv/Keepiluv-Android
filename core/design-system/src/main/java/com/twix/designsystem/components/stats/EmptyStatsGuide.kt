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
import androidx.compose.ui.res.stringResource
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
    imageResId: Int,
    messageResId: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(112.dp))

        Image(
            imageVector = ImageVector.vectorResource(imageResId),
            contentDescription = null,
        )

        Spacer(Modifier.height(8.dp))

        AppText(
            text = stringResource(messageResId),
            style = AppTextStyle.T2,
            color = GrayColor.C400,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmptyStatsGuidePreview() {
    TwixTheme {
        EmptyStatsGuide(
            imageResId = R.drawable.ic_empty_trash,
            messageResId = R.string.stats_stamp_not_has_in_progress_goal,
        )
    }
}
