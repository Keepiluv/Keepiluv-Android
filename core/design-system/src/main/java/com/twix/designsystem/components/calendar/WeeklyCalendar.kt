package com.twix.designsystem.components.calendar

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.runtime.mutableStateOf
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

private enum class WeekSwipeDirection {
    PREVIOUS,
    NEXT,
    NONE,
}

@SuppressLint("UnusedContentLambdaTargetStateParameter")
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

    var dragSumPx by remember { mutableFloatStateOf(0f) }
    var transitionDirection by remember { mutableStateOf(WeekSwipeDirection.NONE) }

    val visibleWeekKey =
        remember(referenceDate) {
            referenceDate.weekStartSunday().toEpochDay().toInt()
        }

    LaunchedEffect(referenceDate, transitionDirection) {
        val weekStart = referenceDate.weekStartSunday()
        when (transitionDirection) {
            WeekSwipeDirection.PREVIOUS -> onUpdateVisibleDate(weekStart)
            WeekSwipeDirection.NEXT -> onUpdateVisibleDate(weekStart.plusDays(6))
            WeekSwipeDirection.NONE -> Unit
        }
    }

    LaunchedEffect(visibleWeekKey) {
        transitionDirection = WeekSwipeDirection.NONE
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .pointerInput(referenceDate) {
                    detectHorizontalDragGestures(
                        onDragStart = { dragSumPx = 0f },
                        onHorizontalDrag = { _, dragAmount ->
                            dragSumPx += dragAmount
                        },
                        onDragEnd = {
                            if (abs(dragSumPx) < 120f) {
                                transitionDirection = WeekSwipeDirection.NONE
                                return@detectHorizontalDragGestures
                            }

                            if (dragSumPx > 0f) {
                                transitionDirection = WeekSwipeDirection.PREVIOUS
                                onPreviousWeek()
                            } else {
                                transitionDirection = WeekSwipeDirection.NEXT
                                onNextWeek()
                            }

                            dragSumPx = 0f
                        },
                        onDragCancel = {
                            dragSumPx = 0f
                            transitionDirection = WeekSwipeDirection.NONE
                        },
                    )
                }.padding(horizontal = 12.dp),
    ) {
        AnimatedContent(
            targetState = visibleWeekKey,
            transitionSpec = {
                val isNext = transitionDirection == WeekSwipeDirection.NEXT

                ContentTransform(
                    targetContentEnter =
                        slideInHorizontally(
                            initialOffsetX = { fullWidth ->
                                if (isNext) fullWidth else -fullWidth
                            },
                            animationSpec = tween(durationMillis = 280),
                        ) + fadeIn(animationSpec = tween(durationMillis = 220)),
                    initialContentExit =
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth ->
                                if (isNext) -fullWidth else fullWidth
                            },
                            animationSpec = tween(durationMillis = 280),
                        ) + fadeOut(animationSpec = tween(durationMillis = 180)),
                    sizeTransform = SizeTransform(clip = false),
                )
            },
        ) { targetWeekKey ->
            val animatedWeekStart =
                remember(targetWeekKey) {
                    LocalDate.ofEpochDay(targetWeekKey.toLong())
                }
            val animatedDays =
                remember(animatedWeekStart) {
                    (0..6).map { animatedWeekStart.plusDays(it.toLong()) }
                }

            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                animatedDays.forEachIndexed { index, date ->
                    val header =
                        if (date == today) stringResource(R.string.word_today) else dayLabels[index]

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
                    .size(40.dp)
                    .then(
                        if (selected) Modifier.border(1.dp, GrayColor.C500, CircleShape) else Modifier,
                    ),
            contentAlignment = Alignment.Center,
        ) {
            AppText(
                text = dayOfMonth.toString(),
                style = AppTextStyle.B1,
                color = GrayColor.C500,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                        .noRippleClickable { onClick() },
            )
        }
    }
}
