package com.twix.designsystem.components.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
fun Calendar(
    initialDate: LocalDate,
    onComplete: (LocalDate) -> Unit,
) {
    // 헤더의 날짜를 나타내는 상태 변수
    var headerDate by rememberSaveable { mutableStateOf(initialDate) }
    // 달력의 선택된 날짜를 나타내는 상태 변수
    var selectedDate by rememberSaveable { mutableStateOf(initialDate) }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(CommonColor.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CalendarNavigator(
            currentDate = headerDate,
            onNextMonth = { headerDate = headerDate.plusMonths(1) },
            onPreviousMonth = { headerDate = headerDate.minusMonths(1) },
        )

        Spacer(Modifier.height(24.dp))

        CalendarContent(
            headerDate = headerDate,
            selectedDate = selectedDate,
            onDateClick = { selectedDate = it },
        )

        Spacer(Modifier.height(40.dp))

        AppButton(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
            text = stringResource(R.string.word_completion),
            onClick = { onComplete(selectedDate) },
        )
    }
}

@Composable
private fun CalendarContent(
    headerDate: LocalDate,
    selectedDate: LocalDate,
    cellSize: Dp = 40.dp,
    onDateClick: (LocalDate) -> Unit,
) {
    val grid =
        remember(headerDate) {
            buildMonthGrid(initialDate = headerDate)
        }

    val dayLabels =
        rotateLabels(
            labels =
                listOf(
                    stringResource(R.string.word_sunday),
                    stringResource(R.string.word_monday),
                    stringResource(R.string.word_tuesday),
                    stringResource(R.string.word_wednesday),
                    stringResource(R.string.word_thursday),
                    stringResource(R.string.word_friday),
                    stringResource(R.string.word_saturday),
                ),
        )

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            dayLabels.forEach { label ->
                AppText(
                    text = label,
                    style = AppTextStyle.C1,
                    color = GrayColor.C300,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(cellSize),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            for (week in 0 until grid.weeks) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    for (d in 0 until 7) {
                        val index = week * 7 + d
                        val date = grid.days[index]
                        val inMonth = date.month == grid.month.month && date.year == grid.month.year
                        val isSelected = date == selectedDate

                        DayCell(
                            date = date,
                            inMonth = inMonth,
                            isSelected = isSelected,
                            onClick = { onDateClick(date) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    inMonth: Boolean, // 현재 보고 있는 월에 포함되는 날짜인지 나타내는 변수
    isSelected: Boolean,
    cellSize: Dp = 40.dp,
    onClick: () -> Unit,
) {
    val dayTextColor =
        when {
            isSelected -> CommonColor.White
            inMonth -> GrayColor.C500
            else -> GrayColor.C200
        }

    Box(
        modifier =
            Modifier
                .size(cellSize)
                .clip(CircleShape)
                .background(if (isSelected) GrayColor.C500 else CommonColor.White)
                .noRippleClickable(enabled = inMonth, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = date.dayOfMonth.toString(),
            style = AppTextStyle.B1,
            color = dayTextColor,
            textAlign = TextAlign.Center,
        )
    }
}
