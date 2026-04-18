package com.twix.goal_manage.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.goal.GoalSummary
import com.twix.ui.base.State
import java.time.LocalDate

@Immutable
data class GoalManageUiState(
    val isInitialized: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val referenceDate: LocalDate = LocalDate.now(), // 7일 달력을 생성하기 위한 레퍼런스 날짜
    val goalSummaries: List<GoalSummary> = emptyList(),
    val pendingGoalIds: Set<Long> = emptySet(), // 중복 요청 방지
    val openedMenuGoalId: Long? = null, // 팝업 현재 열린 goalId
    val endDialog: GoalDialogState? = null,
    val deleteDialog: GoalDialogState? = null,
) : State
