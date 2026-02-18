package com.twix.domain.model.photolog

import com.twix.domain.model.enums.GoalIconType

data class GoalPhotolog(
    val goalId: Long,
    val goalName: String,
    val icon: GoalIconType,
    val myPhotolog: PhotologDetail?,
    val partnerPhotolog: PhotologDetail?,
)
