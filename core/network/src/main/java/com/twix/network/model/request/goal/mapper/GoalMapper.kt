package com.twix.network.model.request.goal.mapper

import com.twix.domain.model.goal.CreateGoalParam
import com.twix.domain.model.goal.UpdateGoalParam
import com.twix.network.model.request.goal.model.CreateGoalRequest
import com.twix.network.model.request.goal.model.UpdateGoalRequest

fun CreateGoalParam.toRequest(): CreateGoalRequest =
    CreateGoalRequest(
        name = name,
        icon = icon.toApi(),
        repeatCycle = repeatCycle.toApi(),
        repeatCount = repeatCount,
        startDate = startDate.toString(),
        endDate = endDate?.toString(),
    )

fun UpdateGoalParam.toRequest(): UpdateGoalRequest =
    UpdateGoalRequest(
        name = name,
        icon = icon.toApi(),
        repeatCycle = repeatCycle.toApi(),
        repeatCount = repeatCount,
        endDate = endDate?.toString(),
    )
