package com.twix.designsystem.components.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.stats.ParticipantStats

@Composable
fun StampCell(
    stats: ParticipantStats,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    Column(modifier = modifier) {
        Row {
            AppText(
                text = stats.nickname,
                style = AppTextStyle.B4,
                color = GrayColor.C500,
            )

            Spacer(modifier = Modifier.weight(1f))

            AppText(
                text = stringResource(R.string.stats_stamp_end_count).format(stats.endCount),
                style = AppTextStyle.B4,
                color = GrayColor.C500,
            )
        }

        content?.invoke()
    }
}

@Preview(showBackground = true)
@Composable
private fun StampCellPreview() {
    TwixTheme {
        StampCell(
            stats =
                ParticipantStats(
                    nickname = "페토",
                    endCount = 6,
                    stampColors = listOf(StampColor.PINK400),
                ),
        )
    }
}
