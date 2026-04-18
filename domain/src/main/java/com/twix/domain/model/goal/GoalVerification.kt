package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalReactionType

data class GoalVerification(
    val photologId: Long,
    val imageUrl: String,
    val comment: String?,
    val reaction: GoalReactionType?,
    val uploadedAt: String,
)
