package com.twix.designsystem.components.calendar

import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import com.twix.ui.extension.weekStartSunday
import java.time.LocalDate
import kotlin.math.abs

@Composable
fun WeeklyCalendar(
    selectedDate: LocalDate,
    referenceDate: LocalDate,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onUpdateVisibleDate: (LocalDate) -> Unit = {},
) {
    val today = remember { LocalDate.now() }
    val weekStart = remember(referenceDate) { referenceDate.weekStartSunday() }
    val days = remember(weekStart) { (0..6).map { weekStart.plusDays(it.toLong()) } }

    val dayLabels =
        listOf(
            stringResource(R.string.word_sunday),
            stringResource(R.string.word_monday),
            stringResource(R.string.word_tuesday),
            stringResource(R.string.word_wednesday),
            stringResource(R.string.word_thursday),
            stringResource(R.string.word_friday),
            stringResource(R.string.word_saturday),
        )
    // 스와이프 처리용 누적 드래그
    var dragSumPx by remember { mutableFloatStateOf(0f) }

    LaunchedEffect(days.first()) {
        if (dragSumPx > 0) {
            onUpdateVisibleDate(days.first())
        } else if (dragSumPx < 0) {
            onUpdateVisibleDate(days.last())
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = { dragSumPx = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            dragSumPx += dragAmount
                        },
                        onDragEnd = {
                            if (abs(dragSumPx) < 120f) return@detectHorizontalDragGestures

                            if (dragSumPx > 0f) onPreviousWeek() else onNextWeek()
                        },
                    )
                }.padding(horizontal = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            days.forEachIndexed { index, date ->
                val header = if (date == today) stringResource(R.string.word_today) else dayLabels[index]

                WeekDayCell(
                    header = header,
                    dayOfMonth = date.dayOfMonth,
                    selected = date == selectedDate,
                    onClick = { onSelectDate(date) },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun WeekDayCell(
    header: String,
    dayOfMonth: Int,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AppText(
            text = header,
            style = AppTextStyle.C1,
            color = GrayColor.C300,
            modifier = Modifier.padding(bottom = 10.dp),
        )

        Box(
            modifier =
                Modifier
                    .size(44.dp)
                    .then(
                        if (selected) Modifier.border(1.dp, GrayColor.C500, CircleShape) else Modifier,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            AppText(
                text = dayOfMonth.toString(),
                style = AppTextStyle.B1,
                color = GrayColor.C400,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .noRippleClickable { onClick() },
            )
        }
    }
}
