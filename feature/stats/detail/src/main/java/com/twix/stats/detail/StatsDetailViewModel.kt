package com.twix.stats.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.domain.repository.GoalRepository
import com.twix.domain.repository.StatsRepository
import com.twix.navigation.NavRoutes
import com.twix.result.AppResult
import com.twix.stats.detail.contract.StatsDetailSideEffect
import com.twix.stats.detail.contract.StatsDetailUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.StatsRefreshBus
import com.yapp.stats.detail.contract.StatsDetailIntent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
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

    private val argDate: String =
        requireNotNull(savedStateHandle[NavRoutes.StatsDetailRoute.ARG_DATE]) { SELECTED_DATE_NOT_FOUND }

    private val cache = mutableMapOf<YearMonth, StatsDetail>()

    private val monthChangeFlow =
        MutableSharedFlow<YearMonth>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    init {
        reduceNavArguments()
        fetchInitialStatsDetail()
        collectMonthChangeFlow()
    }

    private fun collectMonthChangeFlow() {
        viewModelScope.launch {
            monthChangeFlow
                .distinctUntilChanged()
                .debounce(DEBOUNCE_INTERVAL)
                .collectLatest { yearMonth ->
                    fetchStatsDetail(yearMonth)
                }
        }
    }

    private fun reduceNavArguments() {
        reduce {
            copy(
                goalId = argGoalId,
            )
        }
    }

    private fun fetchInitialStatsDetail() {
        val initialDate = LocalDate.parse(argDate).let(YearMonth::from)

        viewModelScope.launch {
            val summaryDeferred = async { statsRepository.fetchStatsSummary(currentState.goalId) }
            val detailDeferred =
                async { statsRepository.fetchStatsDetail(currentState.goalId, initialDate) }

            handleInitialSummary(summaryDeferred.await())
            handleInitialDetail(detailDeferred.await(), initialDate)
        }
    }

    private suspend fun handleInitialSummary(result: AppResult<StatsSummary>) {
        when (result) {
            is AppResult.Success -> reduce { copy(summary = result.data) }
            is AppResult.Error -> {
                handleError(result.error)
                showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
            }
        }
    }

    private suspend fun handleInitialDetail(
        result: AppResult<StatsDetail>,
        initialDate: YearMonth,
    ) {
        when (result) {
            is AppResult.Success -> handleFetchStatsDetailSuccess(result.data)
            is AppResult.Error -> {
                handleError(result.error)
                reduceDetailWithEmptyCompletedDate(initialDate)
                showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
            }
        }
    }

    private fun fetchStatsDetail(date: YearMonth) {
        if (checkCache(date)) return
        launchResult(
            block = { statsRepository.fetchStatsDetail(currentState.goalId, date) },
            onSuccess = { handleFetchStatsDetailSuccess(it) },
            onError = {
                reduceDetailWithEmptyCompletedDate(date)
                showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
            },
        )
    }

    private fun handleFetchStatsDetailSuccess(result: StatsDetail) {
        cache[YearMonth.from(result.yearMonth)] = result
        reduce {
            copy(
                detail = result,
                calendarUiModel =
                    StatsCalendarUiModel.create(
                        currentDate = result.yearMonth,
                        completedDate = result.completedDate,
                    ),
            )
        }
    }

    private fun reduceDetailWithEmptyCompletedDate(date: YearMonth) {
        val currentDate = date.atDay(1)
        reduce {
            copy(
                detail =
                    detail.copy(
                        yearMonth = currentDate,
                        completedDate = emptyList(),
                    ),
                calendarUiModel =
                    StatsCalendarUiModel.create(
                        currentDate = currentDate,
                        completedDate = emptyList(),
                    ),
            )
        }
    }

    private fun checkCache(yearMonth: YearMonth): Boolean {
        cache[yearMonth]?.let {
            reduce {
                copy(
                    detail = it,
                    calendarUiModel =
                        StatsCalendarUiModel.create(
                            currentDate = it.yearMonth,
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
            is StatsDetailIntent.SelectDate -> navigateToTaskCertificationDetail(intent.date)
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
        val previousMonth = currentState.detail.yearMonth.minusMonths(1)
        reduce { copy(detail = detail.copy(yearMonth = previousMonth)) }
        monthChangeFlow.tryEmit(YearMonth.from(previousMonth))
    }

    private fun fetchNextMonth() {
        val nextMonth = currentState.detail.yearMonth.plusMonths(1)
        reduce { copy(detail = detail.copy(yearMonth = nextMonth)) }
        monthChangeFlow.tryEmit(YearMonth.from(nextMonth))
    }

    private fun navigateToTaskCertificationDetail(date: LocalDate) {
        viewModelScope.launch {
            emitSideEffect(
                StatsDetailSideEffect.NavigateToTaskCertificationDetail(
                    goalId = currentState.goalId,
                    date = date,
                    betweenUs = currentState.resolveBetweenUs(date),
                ),
            )
        }
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
                    when (currentState.detail.isCompleted) {
                        true -> StatsRefreshBus.Publisher.End
                        else -> StatsRefreshBus.Publisher.InProgress
                    }
                statsRefreshBus.notifyChanged(publisher)
                tryEmitSideEffect(StatsDetailSideEffect.NavigateToBack)
            },
            onError = { showToast(R.string.toast_delete_goal_failed, ToastType.ERROR) },
        )
    }

    private suspend fun showToast(
        message: Int,
        type: ToastType,
    ) {
        emitSideEffect(StatsDetailSideEffect.ShowToast(message, type))
    }

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val SELECTED_DATE_NOT_FOUND = "Selected Date Argument Not Found"
        private const val DEBOUNCE_INTERVAL = 300L
    }
}
