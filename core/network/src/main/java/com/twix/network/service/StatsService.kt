package com.twix.network.service

import com.twix.network.model.response.stats.model.StatsCalendarResponse
import com.twix.network.model.response.stats.model.StatsResponse
import com.twix.network.model.response.stats.model.StatsSummaryResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import java.time.YearMonth

interface StatsService {
    @GET("api/v1/stats")
    suspend fun fetchStats(
        @Query("selectedDate") selectedDate: YearMonth,
        @Query("status") status: String,
    ): StatsResponse

    @GET("api/v1/stats/{goalId}/calendar")
    suspend fun fetchStatsCalendar(
        @Path("goalId") goalId: Long,
        @Query("selectedDate") selectedDate: YearMonth,
    ): StatsCalendarResponse

    @GET("api/v1/stats/{goalId}/summary")
    suspend fun fetchStatsSummary(
        @Path("goalId") goalId: Long,
    ): StatsSummaryResponse
}
