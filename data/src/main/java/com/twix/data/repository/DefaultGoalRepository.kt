package com.twix.data.repository

import com.twix.domain.model.goal.CreateGoalParam
import com.twix.domain.model.goal.GoalDetail
import com.twix.domain.model.goal.GoalList
import com.twix.domain.model.goal.GoalSummary
import com.twix.domain.model.goal.UpdateGoalParam
import com.twix.domain.repository.GoalRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.goal.mapper.toRequest
import com.twix.network.model.response.goal.mapper.toDomain
import com.twix.network.service.GoalService
import com.twix.result.AppResult

class DefaultGoalRepository(
    private val service: GoalService,
) : GoalRepository {
    override suspend fun fetchGoalList(date: String): AppResult<GoalList> = safeApiCall { service.fetchGoals(date).toDomain() }

    override suspend fun createGoal(param: CreateGoalParam): AppResult<GoalDetail> =
        safeApiCall {
            service.createGoal(param.toRequest()).toDomain()
        }

    override suspend fun updateGoal(param: UpdateGoalParam): AppResult<GoalDetail> =
        safeApiCall {
            service.updateGoal(body = param.toRequest(), goalId = param.goalId).toDomain()
        }

    override suspend fun fetchGoalDetail(goalId: Long): AppResult<GoalDetail> =
        safeApiCall {
            service.fetchGoalDetail(goalId).toDomain()
        }

    override suspend fun deleteGoal(goalId: Long): AppResult<Unit> = safeApiCall { service.deleteGoal(goalId) }

    override suspend fun completeGoal(goalId: Long): AppResult<Unit> = safeApiCall { service.completeGoal(goalId) }

    override suspend fun fetchGoalSummaryList(date: String): AppResult<List<GoalSummary>> =
        safeApiCall {
            service.fetchGoalSummaryList(date).toDomain()
        }
}
