package com.twix.stats.model

import androidx.annotation.StringRes
import com.twix.designsystem.R

enum class StatsTabDestination(
    @field:StringRes
    val label: Int,
) {
    IN_PROGRESS(R.string.stats_stamp_in_progress_tap_title),
    END(R.string.stats_stamp_end_tap_title),
}
