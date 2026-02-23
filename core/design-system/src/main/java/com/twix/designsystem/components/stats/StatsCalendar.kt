package com.twix.designsystem.components.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.stats.detail.CompletedDate
import java.time.LocalDate

@Composable
fun StatsCalendar(
    uiModel: StatsCalendarUiModel,
    onSelectedDate: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        DayOfWeekHeader()

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            uiModel.weeks.forEach { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    week.forEach { date ->
                        if (date == null) {
                            Box(modifier = Modifier.size(44.dp))
                        } else {
                            PictureDayCell(
                                date = date,
                                completed = uiModel.completedDateMap[date],
                                onDateSelected = { onSelectedDate(it) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }

                    if (week.size < 7) {
                        repeat(7 - week.size) {
                            Box(modifier = Modifier.size(44.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DayOfWeekHeader() {
    val days =
        listOf(
            stringResource(R.string.word_sunday),
            stringResource(R.string.word_monday),
            stringResource(R.string.word_tuesday),
            stringResource(R.string.word_wednesday),
            stringResource(R.string.word_thursday),
            stringResource(R.string.word_friday),
            stringResource(R.string.word_saturday),
        )
    Row(
        modifier = Modifier.fillMaxWidth(),
    ) {
        days.forEach { day ->
            AppText(
                text = day,
                style = AppTextStyle.B2,
                color = GrayColor.C300,
                modifier =
                    Modifier
                        .weight(1f)
                        .height(24.dp),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsCalendarPreview() {
    TwixTheme {
        StatsCalendar(
            uiModel =
                StatsCalendarUiModel.create(
                    currentDate = LocalDate.of(2026, 2, 1),
                    completedDate =
                        listOf(
                            CompletedDate(
                                LocalDate.of(2026, 2, 1),
                                "https://picsum.photos/100",
                                "https://picsum.photos/100",
                            ),
                            CompletedDate(
                                LocalDate.of(2026, 2, 3),
                                "https://picsum.photos/101",
                                null,
                            ),
                            CompletedDate(
                                LocalDate.of(2026, 2, 15),
                                "https://picsum.photos/102",
                                "https://picsum.photos/102",
                            ),
                            CompletedDate(
                                LocalDate.of(2026, 2, 28),
                                null,
                                "https://picsum.photos/103",
                            ),
                        ),
                ),
            onSelectedDate = { },
        )
    }
}
