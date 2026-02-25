package com.twix.domain.repository

import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.result.AppResult
import java.time.LocalDate

interface StatsRepository {
    suspend fun fetchInProgressStats(date: LocalDate): AppResult<Stats>

    suspend fun fetchEndStats(): AppResult<List<StatsGoal>>

    suspend fun fetchStatsDetail(
        goalId: Long,
        date: LocalDate?,
    ): AppResult<StatsDetail>
}
