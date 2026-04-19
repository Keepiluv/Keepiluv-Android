package com.twix.stats.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.extension.label
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.stats.detail.StatsSummary
import java.time.LocalDate

@Composable
fun SummaryContent(
    statsSummary: StatsSummary,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(horizontal = 20.dp),
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = GrayColor.C500,
        )

        Spacer(Modifier.height(20.dp))

        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppText(
                    text = stringResource(R.string.word_end_count),
                    style = AppTextStyle.C1,
                    color = GrayColor.C400,
                    modifier = Modifier.width(56.dp),
                )
                Spacer(Modifier.width(28.dp))
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_checked_you),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                    )

                    AppText(
                        text =
                            stringResource(
                                R.string.stats_complete_count,
                                statsSummary.myNickname,
                                statsSummary.myCompletedCount,
                                statsSummary.totalCount,
                            ),
                        style = AppTextStyle.B4,
                        color = GrayColor.C500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    VerticalDivider(
                        modifier = Modifier.height(15.dp),
                        thickness = 1.dp,
                        color = GrayColor.C200,
                    )

                    AppText(
                        text =
                            stringResource(
                                R.string.stats_complete_count,
                                statsSummary.partnerNickname,
                                statsSummary.partnerCompletedCount,
                                statsSummary.totalCount,
                            ),
                        style = AppTextStyle.B4,
                        color = GrayColor.C500,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            SummaryRow(
                label = stringResource(R.string.word_repeat_type),
                value = statsSummary.repeatCycle.label(),
            )
            SummaryRow(
                label = stringResource(R.string.word_start_date),
                value =
                    stringResource(
                        R.string.date_year_month_day_no_padding,
                        statsSummary.startDate.year,
                        statsSummary.startDate.monthValue,
                        statsSummary.startDate.dayOfMonth,
                    ),
            )
            SummaryRow(
                label = stringResource(R.string.word_end_date),
                value =
                    statsSummary.endDate?.let {
                        stringResource(
                            R.string.date_year_month_day_no_padding,
                            it.year,
                            it.monthValue,
                            it.dayOfMonth,
                        )
                    } ?: stringResource(R.string.word_not_set),
            )
        }

        Spacer(Modifier.height(20.dp))

        HorizontalDivider(
            thickness = 1.dp,
            color = GrayColor.C500,
        )

        Spacer(Modifier.height(52.dp))
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        AppText(
            text = label,
            style = AppTextStyle.C1,
            color = GrayColor.C400,
            modifier = Modifier.width(56.dp),
        )
        Spacer(Modifier.width(28.dp))
        AppText(
            text = value,
            style = AppTextStyle.B4,
            color = GrayColor.C500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun SummaryContentPreview() {
    TwixTheme {
        SummaryContent(
            statsSummary =
                StatsSummary(
                    myNickname = "페토페토페토페토페토페토",
                    myCompletedCount = 10,
                    partnerNickname = "찬호찬호찬호찬호찬호찬호찬호",
                    partnerCompletedCount = 8,
                    totalCount = 20,
                    repeatCycle = RepeatCycle.DAILY,
                    startDate = LocalDate.of(2025, 11, 12),
                    endDate = LocalDate.now(),
                ),
        )
    }
}
