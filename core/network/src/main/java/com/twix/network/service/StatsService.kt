package com.twix.network.service

import com.twix.network.model.response.stats.model.StatsResponse
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import java.time.YearMonth

interface StatsService {
    @GET("api/v1/stats")
    suspend fun fetchStats(
        @Query("selectedDate") selectedDate: YearMonth,
        @Query("status") status: String,
    ): StatsResponse
}
