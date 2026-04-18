package com.twix.network.service

import com.twix.network.model.request.goal.model.CreateGoalRequest
import com.twix.network.model.request.goal.model.UpdateGoalRequest
import com.twix.network.model.response.goal.model.GoalDetailResponse
import com.twix.network.model.response.goal.model.GoalListResponse
import com.twix.network.model.response.goal.model.GoalSummaryListResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

interface GoalService {
    @GET("api/v1/goals")
    suspend fun fetchGoals(
        @Query("date") date: String,
    ): GoalListResponse

    @POST("api/v1/goals")
    suspend fun createGoal(
        @Body body: CreateGoalRequest,
    ): GoalDetailResponse

    @PUT("api/v1/goals/{goalId}")
    suspend fun updateGoal(
        @Body body: UpdateGoalRequest,
        @Path("goalId") goalId: Long,
    ): GoalDetailResponse

    @GET("api/v1/goals/{goalId}")
    suspend fun fetchGoalDetail(
        @Path("goalId") goalId: Long,
    ): GoalDetailResponse

    @DELETE("api/v1/goals/{goalId}")
    suspend fun deleteGoal(
        @Path("goalId") goalId: Long,
    )

    @PATCH("api/v1/goals/{goalId}/complete")
    suspend fun completeGoal(
        @Path("goalId") goalId: Long,
    )

    @GET("api/v1/goals/detail")
    suspend fun fetchGoalSummaryList(
        @Query("date") date: String,
    ): GoalSummaryListResponse
}
