package com.twix.domain.model.goal

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import java.time.LocalDate

data class GoalSummary(
    val goalId: Long = -1L,
    val name: String = "",
    val icon: GoalIconType = GoalIconType.DEFAULT,
    val repeatCycle: RepeatCycle = RepeatCycle.DAILY,
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate? = null,
)
