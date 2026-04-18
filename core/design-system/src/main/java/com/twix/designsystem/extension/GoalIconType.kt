package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.domain.model.enums.GoalIconType

fun GoalIconType.toRes(): Int =
    when (this) {
        GoalIconType.DEFAULT -> R.drawable.ic_default
        GoalIconType.CLEAN -> R.drawable.ic_clean
        GoalIconType.EXERCISE -> R.drawable.ic_exercise
        GoalIconType.BOOK -> R.drawable.ic_book
        GoalIconType.PENCIL -> R.drawable.ic_pencil
        GoalIconType.HEALTH -> R.drawable.ic_health
        GoalIconType.HEART -> R.drawable.ic_heart
        GoalIconType.LAPTOP -> R.drawable.ic_laptop
    }
