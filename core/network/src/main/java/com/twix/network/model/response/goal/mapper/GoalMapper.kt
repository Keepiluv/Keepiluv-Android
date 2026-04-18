package com.twix.network.model.response.goal.mapper

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.goal.Goal
import com.twix.domain.model.goal.GoalDetail
import com.twix.domain.model.goal.GoalList
import com.twix.domain.model.goal.GoalSummary
import com.twix.domain.model.goal.GoalVerification
import com.twix.network.model.response.goal.model.GoalDetailResponse
import com.twix.network.model.response.goal.model.GoalListResponse
import com.twix.network.model.response.goal.model.GoalResponse
import com.twix.network.model.response.goal.model.GoalSummaryListResponse
import com.twix.network.model.response.goal.model.VerificationResponse
import java.time.LocalDate

fun GoalListResponse.toDomain(): GoalList =
    GoalList(
        completedCount = completedCount,
        totalCount = totalCount,
        goals = goals.map { it.toDomain() },
    )

fun GoalResponse.toDomain(): Goal =
    Goal(
        goalId = goalId,
        name = name,
        icon = GoalIconType.fromApi(icon),
        repeatCycle = RepeatCycle.fromApi(repeatCycle),
        myCompleted = myCompleted,
        partnerCompleted = partnerCompleted,
        myVerification = myVerification?.toDomainOrNull(),
        partnerVerification = partnerVerification?.toDomainOrNull(),
    )

fun VerificationResponse.toDomainOrNull(): GoalVerification? =
    GoalVerification(
        photologId = photologId,
        imageUrl = imageUrl,
        comment = comment,
        reaction = GoalReactionType.fromApi(reaction),
        uploadedAt = uploadedAt,
    )

fun GoalDetailResponse.toDomain(): GoalDetail =
    GoalDetail(
        goalId = goalId,
        name = name,
        icon = GoalIconType.fromApi(icon),
        repeatCycle = RepeatCycle.fromApi(repeatCycle),
        repeatCount = repeatCount,
        startDate = LocalDate.parse(startDate),
        endDate = endDate?.let(LocalDate::parse),
        createdAt = createdAt,
    )

fun GoalSummaryListResponse.toDomain(): List<GoalSummary> =
    this.goals.map {
        GoalSummary(
            goalId = it.goalId,
            name = it.name,
            icon = GoalIconType.fromApi(it.icon),
            repeatCycle = RepeatCycle.fromApi(it.repeatCycle),
            startDate = LocalDate.parse(it.startDate),
            it.endDate?.let(LocalDate::parse),
        )
    }
