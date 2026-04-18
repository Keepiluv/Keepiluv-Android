package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import java.time.LocalDate

data class GoalDetail(
    val goalId: Long,
    val name: String,
    val icon: GoalIconType,
    val repeatCycle: RepeatCycle,
    val repeatCount: Int,
    val startDate: LocalDate,
    val endDate: LocalDate?,
    val createdAt: String,
)
