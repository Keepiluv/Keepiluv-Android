package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.domain.model.enums.GoalReactionType

fun GoalReactionType.toRes(): Int =
    when (this) {
        GoalReactionType.HAPPY -> R.drawable.ic_happy
        GoalReactionType.TROUBLE -> R.drawable.ic_trouble
        GoalReactionType.LOVE -> R.drawable.ic_love
        GoalReactionType.DOUBT -> R.drawable.ic_doubt
        GoalReactionType.FUCK -> R.drawable.ic_fuck
    }
