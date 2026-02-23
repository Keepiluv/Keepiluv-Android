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
) {
    companion object {
        val EMPTY =
            StatsSummary(
                myNickname = "",
                partnerNickname = "",
                totalCount = 0,
                myCompletedCount = 0,
                partnerCompletedCount = 0,
                repeatCycle = RepeatCycle.DAILY,
                startDate = LocalDate.now(),
                endDate = null,
            )
    }
}
