package com.twix.designsystem.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.twix.designsystem.R
import com.twix.domain.model.enums.RepeatCycle

@Composable
fun RepeatCycle.label(): String =
    when (this) {
        RepeatCycle.DAILY -> stringResource(R.string.word_daily)
        RepeatCycle.WEEKLY -> stringResource(R.string.word_weekly)
        RepeatCycle.MONTHLY -> stringResource(R.string.word_monthly)
    }
