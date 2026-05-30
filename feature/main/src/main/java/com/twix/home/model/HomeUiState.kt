package com.twix.home.model

import androidx.compose.runtime.Immutable
import com.twix.domain.model.goal.GoalList
import com.twix.result.AppError
import com.twix.ui.base.ContentLoadableState
import java.time.LocalDate
import java.time.YearMonth

@Immutable
data class HomeUiState(
    val month: YearMonth = YearMonth.now(),
    val visibleDate: LocalDate = LocalDate.now(), // 홈 화면 상단에 존재하는 월, 년 텍스트를 위한 상태 변수
    val selectedDate: LocalDate = LocalDate.now(),
    val referenceDate: LocalDate = LocalDate.now(), // 7일 달력을 생성하기 위한 레퍼런스 날짜
    val goalList: GoalList = GoalList(),
    val selectedGoalId: Long = -1,
    val isRefreshing: Boolean = false, // 당겨서 리프레시에 사용
    override val hasLoadedContent: Boolean = false,
    override val isLoading: Boolean = false,
    override val error: AppError? = null,
) : ContentLoadableState {
    val monthYear: String
        get() = "${visibleDate.month.value}월 ${visibleDate.year}"

    val showContentLoading get() = showOverlayLoading

    val showEmpty get() = hasLoadedContent && goalList.goals.isEmpty() && error == null

    override fun copyState(
        isLoading: Boolean,
        error: AppError?,
    ): ContentLoadableState = copy(isLoading = isLoading, error = error)
}
