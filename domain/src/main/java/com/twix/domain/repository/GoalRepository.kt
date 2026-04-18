package com.twix.domain.repository

import com.twix.domain.model.goal.CreateGoalParam
import com.twix.domain.model.goal.GoalDetail
import com.twix.domain.model.goal.GoalList
import com.twix.domain.model.goal.GoalSummary
import com.twix.domain.model.goal.UpdateGoalParam
import com.twix.result.AppResult

interface GoalRepository {
    suspend fun fetchGoalList(date: String): AppResult<GoalList>

    suspend fun createGoal(param: CreateGoalParam): AppResult<GoalDetail>

    suspend fun updateGoal(param: UpdateGoalParam): AppResult<GoalDetail>

    suspend fun fetchGoalDetail(goalId: Long): AppResult<GoalDetail>

    suspend fun deleteGoal(goalId: Long): AppResult<Unit>

    suspend fun completeGoal(goalId: Long): AppResult<Unit>

    suspend fun fetchGoalSummaryList(date: String): AppResult<List<GoalSummary>>
}
