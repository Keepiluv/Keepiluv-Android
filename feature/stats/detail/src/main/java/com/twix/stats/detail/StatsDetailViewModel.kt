package com.twix.stats.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.repository.GoalRepository
import com.twix.domain.repository.StatsRepository
import com.twix.navigation.NavRoutes
import com.twix.stats.detail.contract.StatsDetailSideEffect
import com.twix.stats.detail.contract.StatsDetailUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.StatsRefreshBus
import com.yapp.stats.detail.contract.StatsDetailIntent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth

@OptIn(FlowPreview::class)
class StatsDetailViewModel(
    private val statsRefreshBus: StatsRefreshBus,
    private val goalRepository: GoalRepository,
    private val statsRepository: StatsRepository,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<StatsDetailUiState, StatsDetailIntent, StatsDetailSideEffect>(
        StatsDetailUiState(),
    ) {
    private val argGoalId: Long =
        requireNotNull(savedStateHandle[NavRoutes.StatsDetailRoute.ARG_GOAL_ID]) { GOAL_ID_NOT_FOUND }

    private val argDate: String? = savedStateHandle.get<String>(NavRoutes.StatsDetailRoute.ARG_DATE)

    private val cache = mutableMapOf<YearMonth, StatsDetail>()

    private val monthChangeFlow =
        MutableSharedFlow<YearMonth>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    init {
        collectMonthChangeFlow()
        reduceNavArguments()
        fetchStatsDetail(argDate?.let { LocalDate.parse(it) })
    }

    private fun collectMonthChangeFlow() {
        viewModelScope.launch {
            monthChangeFlow
                .distinctUntilChanged()
                .debounce(DEBOUNCE_INTERVAL)
                .collect { yearMonth -> fetchStatsDetail(yearMonth.atDay(1)) }
        }
    }

    private fun reduceNavArguments() {
        reduce {
            copy(
                goalId = argGoalId,
                isInProgressStatsDetail = argDate != null,
            )
        }
    }

    private fun fetchStatsDetail(date: LocalDate?) {
        val result = date?.let { checkCache(YearMonth.from(it)) }
        if (result == true) return
        launchResult(
            block = { statsRepository.fetchStatsDetail(currentState.goalId, date) },
            onSuccess = {
                cache[YearMonth.from(it.monthDate)] = it
                reduce {
                    copy(
                        detail = it,
                        calendarUiModel =
                            StatsCalendarUiModel.create(
                                currentDate = it.monthDate,
                                completedDate = it.completedDate,
                            ),
                    )
                }
            },
            onError = {
                reduce { copy(detail = detail.copy(monthDate = date ?: LocalDate.now())) }
                showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
            },
        )
    }

    private fun checkCache(yearMonth: YearMonth): Boolean {
        cache[yearMonth]?.let {
            reduce {
                copy(
                    detail = it,
                    calendarUiModel =
                        StatsCalendarUiModel.create(
                            currentDate = it.monthDate,
                            completedDate = it.completedDate,
                        ),
                )
            }
            return true
        }
        return false
    }

    override suspend fun handleIntent(intent: StatsDetailIntent) {
        when (intent) {
            StatsDetailIntent.PreviousMonth -> fetchPreviousMonth()
            StatsDetailIntent.NextMonth -> fetchNextMonth()
            StatsDetailIntent.GoalEnd -> {
                // endGoal()
            }

            StatsDetailIntent.GoalDelete -> {
                // deleteGoal()
            }
        }
    }

    private fun fetchPreviousMonth() {
        val previousMonth = currentState.detail.monthDate.minusMonths(1)
        reduce { copy(detail = detail.copy(monthDate = previousMonth)) }
        monthChangeFlow.tryEmit(YearMonth.from(previousMonth))
    }

    private fun fetchNextMonth() {
        val nextMonth = currentState.detail.monthDate.plusMonths(1)
        reduce { copy(detail = detail.copy(monthDate = nextMonth)) }
        monthChangeFlow.tryEmit(YearMonth.from(nextMonth))
    }

    private fun endGoal() {
        launchResult(
            block = { goalRepository.completeGoal(argGoalId) },
            onSuccess = {
                statsRefreshBus.notifyChanged(StatsRefreshBus.Publisher.InProgress)
                tryEmitSideEffect(StatsDetailSideEffect.NavigateToBack)
            },
            onError = { showToast(R.string.toast_complete_goal_failed, ToastType.ERROR) },
        )
    }

    private fun deleteGoal() {
        launchResult(
            block = { goalRepository.deleteGoal(argGoalId) },
            onSuccess = {
                val publisher =
                    when (currentState.isInProgressStatsDetail) {
                        true -> StatsRefreshBus.Publisher.InProgress
                        else -> StatsRefreshBus.Publisher.End
                    }
                statsRefreshBus.notifyChanged(publisher)
                tryEmitSideEffect(StatsDetailSideEffect.NavigateToBack)
            },
            onError = { showToast(R.string.toast_delete_goal_failed, ToastType.ERROR) },
        )
    }

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(StatsDetailSideEffect.ShowToast(message, type))
        }
    }

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val DEBOUNCE_INTERVAL = 300L
    }
}
