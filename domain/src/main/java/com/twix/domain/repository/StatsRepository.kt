package com.twix.domain.repository

import com.twix.domain.model.enums.StatsStatus
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.result.AppResult
import java.time.YearMonth

interface StatsRepository {
    suspend fun fetchStats(
        date: YearMonth,
        status: StatsStatus,
    ): AppResult<Stats>

    suspend fun fetchStatsDetail(
        goalId: Long,
        date: YearMonth,
    ): AppResult<StatsDetail>

    suspend fun fetchStatsSummary(goalId: Long): AppResult<StatsSummary>
}
