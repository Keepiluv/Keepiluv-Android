package com.twix.goal_editor.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.common.CommonSwitch
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.extension.label
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.RepeatCycle
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
fun GoalInfoCard(
    selectedRepeatCycle: RepeatCycle,
    repeatCount: Int,
    startDate: LocalDate,
    endDateEnabled: Boolean,
    endDate: LocalDate,
    isEdit: Boolean,
    onSelectedRepeatType: (RepeatCycle) -> Unit,
    onShowRepeatCountBottomSheet: () -> Unit,
    onShowCalendarBottomSheet: (Boolean) -> Unit, // true면 endDate
    onToggleEndDateEnabled: (Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .border(1.dp, GrayColor.C500, RoundedCornerShape(12.dp)),
    ) {
        RepeatTypeSettings(
            selectedRepeatCycle = selectedRepeatCycle,
            repeatCount = repeatCount,
            onSelectedRepeatType = onSelectedRepeatType,
            onShowRepeatCountBottomSheet = onShowRepeatCountBottomSheet,
        )

        if (!isEdit) {
            HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

            DateSettings(
                date = startDate,
                onShowCalendarBottomSheet = { onShowCalendarBottomSheet(false) },
            )
        }

        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

        EndDateOption(
            visible = endDateEnabled,
            onToggle = onToggleEndDateEnabled,
        )

        AnimatedVisibility(
            visible = endDateEnabled,
            enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
            exit = shrinkVertically(shrinkTowards = Alignment.Top) + fadeOut(),
        ) {
            Column {
                HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

                DateSettings(
                    date = endDate,
                    isEndDate = true,
                    onShowCalendarBottomSheet = { onShowCalendarBottomSheet(true) },
                )
            }
        }
    }
}

@Composable
private fun RepeatTypeSettings(
    selectedRepeatCycle: RepeatCycle,
    repeatCount: Int,
    onSelectedRepeatType: (RepeatCycle) -> Unit,
    onShowRepeatCountBottomSheet: () -> Unit,
) {
    val animationDuration = 160

    Column(
        modifier =
            Modifier
                .padding(16.dp),
    ) {
        HeaderText(stringResource(R.string.header_repeat_type))

        Spacer(Modifier.height(12.dp))

        Row(
            modifier =
                Modifier
                    .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RepeatCycle.entries.forEachIndexed { index, type ->
                val isSelected = selectedRepeatCycle == type

                AppText(
                    text = type.label(),
                    style = AppTextStyle.B2,
                    color = if (isSelected) CommonColor.White else GrayColor.C500,
                    modifier =
                        Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) GrayColor.C500 else CommonColor.White)
                            .border(1.dp, GrayColor.C500, RoundedCornerShape(8.dp))
                            .padding(horizontal = 12.dp, vertical = 5.5.dp)
                            .noRippleClickable(onClick = { onSelectedRepeatType(type) }),
                )

                if (index != RepeatCycle.entries.lastIndex) Spacer(Modifier.width(8.dp))
            }

            Spacer(Modifier.weight(1f))

            AnimatedVisibility(
                visible = selectedRepeatCycle != RepeatCycle.DAILY,
                enter = fadeIn(animationSpec = tween(durationMillis = animationDuration)),
                exit = fadeOut(animationSpec = tween(durationMillis = animationDuration)),
            ) {
                Row(
                    modifier =
                        Modifier
                            .noRippleClickable(onClick = onShowRepeatCountBottomSheet),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(
                        text = stringResource(R.string.repeat_count, selectedRepeatCycle.label(), repeatCount),
                        style = AppTextStyle.B2,
                        color = GrayColor.C500,
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_chevron_down_circle),
                        contentDescription = "repeat type",
                        modifier =
                            Modifier
                                .size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DateSettings(
    date: LocalDate,
    isEndDate: Boolean = false,
    onShowCalendarBottomSheet: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .padding(16.dp)
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderText(stringResource(if (isEndDate) R.string.header_end_date else R.string.header_start_date))

        Spacer(Modifier.weight(1f))

        Row(
            modifier =
                Modifier
                    .noRippleClickable(onClick = onShowCalendarBottomSheet),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText(
                text = "%s월 %s일".format(date.monthValue, date.dayOfMonth),
                style = AppTextStyle.B2,
                color = GrayColor.C500,
            )

            Image(
                painter = painterResource(R.drawable.ic_chevron_down_circle),
                contentDescription = "date",
                modifier =
                    Modifier
                        .size(24.dp),
            )
        }
    }
}

@Composable
private fun EndDateOption(
    visible: Boolean = false,
    onToggle: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HeaderText(stringResource(R.string.header_end_date_option))

        Spacer(Modifier.weight(1f))

        CommonSwitch(
            checked = visible,
            onClick = onToggle,
        )
    }
}

@Composable
private fun HeaderText(text: String) {
    AppText(
        text = text,
        style = AppTextStyle.B1,
        color = GrayColor.C500,
    )
}
