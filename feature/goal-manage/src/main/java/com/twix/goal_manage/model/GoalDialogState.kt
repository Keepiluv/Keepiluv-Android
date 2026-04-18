package com.twix.goal_manage.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.enums.GoalIconType

@Immutable
data class GoalDialogState(
    val goalId: Long,
    val name: String,
    val icon: GoalIconType,
)
