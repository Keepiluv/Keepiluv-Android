package com.twix.data.repository

import com.twix.domain.model.enums.StatsStatus
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.repository.StatsRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.response.stats.mapper.toDomain
import com.twix.network.service.StatsService
import com.twix.result.AppResult
import java.time.YearMonth

class DefaultStatsRepository(
    private val service: StatsService,
) : StatsRepository {
    override suspend fun fetchStats(
        date: YearMonth,
        status: StatsStatus,
    ): AppResult<Stats> =
        safeApiCall {
            service
                .fetchStats(
                    selectedDate = date,
                    status = status.toApi(),
                ).toDomain()
        }

    override suspend fun fetchStatsDetail(
        goalId: Long,
        date: YearMonth,
    ): AppResult<StatsDetail> {
        TODO("Not yet implemented")
    }
}
