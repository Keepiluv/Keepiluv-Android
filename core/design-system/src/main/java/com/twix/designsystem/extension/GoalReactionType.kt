package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.domain.model.enums.GoalReactionType

fun GoalReactionType.toRes(): Int =
    when (this) {
        GoalReactionType.HAPPY -> R.drawable.ic_emoji_happy
        GoalReactionType.TROUBLE -> R.drawable.ic_emoji_trouble
        GoalReactionType.LOVE -> R.drawable.ic_emoji_love
        GoalReactionType.DOUBT -> R.drawable.ic_emoji_doubt
        GoalReactionType.FUCK -> R.drawable.ic_emoji_fuck
    }
