package com.twix.domain.model.stats.detail

import com.twix.domain.model.enums.RepeatCycle
import java.time.LocalDate

data class StatsSummary(
    val myNickname: String,
    val partnerNickname: String,
    val totalCount: Int,
    val myCompletedCount: Int,
    val partnerCompletedCount: Int,
    val repeatCycle: RepeatCycle,
    val startDate: LocalDate,
    val endDate: LocalDate?,
)
