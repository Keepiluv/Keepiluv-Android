package com.twix.stats.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.stats.model.StatsCalendarUiModel
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.domain.repository.GoalRepository
import com.twix.domain.repository.StatsRepository
import com.twix.navigation.NavRoutes
import com.twix.result.AppResult
import com.twix.result.errorOrNull
import com.twix.stats.detail.contract.StatsDetailSideEffect
import com.twix.stats.detail.contract.StatsDetailUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.StatsDetailRefreshBus
import com.twix.util.bus.StatsRefreshBus
import com.yapp.stats.detail.contract.StatsDetailIntent
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.coroutineScope
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
    private val statsDetailRefreshBus: StatsDetailRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
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
        fetchInitialStats()
        collectMonthChangeFlow()
        collectEventBus()
    }

    private fun fetchInitialStats() {
        val initialDate = LocalDate.parse(argDate).let(YearMonth::from)

        viewModelScope.launch {
            reduce {
                copyState(isLoading = true, error = null) as StatsDetailUiState
            }
            val (summary, detail) = fetchStats(initialDate)
            handleFetchStatsDetailResult(summary, detail, initialDate)
        }
    }

    private suspend fun fetchStats(yearMonth: YearMonth): Pair<AppResult<StatsSummary>, AppResult<StatsDetail>> =
        coroutineScope {
            val summaryDeferred = async { statsRepository.fetchStatsSummary(argGoalId) }
            val detailDeferred = async { statsRepository.fetchStatsDetail(argGoalId, yearMonth) }
            summaryDeferred.await() to detailDeferred.await()
        }

    private fun handleFetchStatsDetailResult(
        summary: AppResult<StatsSummary>,
        detail: AppResult<StatsDetail>,
        initialDate: YearMonth,
    ) {
        if (summary is AppResult.Success && detail is AppResult.Success) {
            reduce {
                copy(
                    summary = summary.data,
                )
            }
            reduceStatsDetail(detail.data)
        } else {
            if (summary is AppResult.Error) handleError(summary.error)
            if (detail is AppResult.Error) handleError(detail.error)
            clearCalendarOnError(initialDate)
            reduce {
                copyState(
                    isLoading = false,
                    error = listOfNotNull(summary.errorOrNull(), detail.errorOrNull()).firstOrNull(),
                ) as StatsDetailUiState
            }
        }
    }

    private fun reduceStatsDetail(result: StatsDetail) {
        cache[YearMonth.from(result.currentDate)] = result
        reduce {
            copy(
                detail = result,
                calendarUiModel =
                    StatsCalendarUiModel.create(
                        currentDate = result.currentDate,
                        completedDate = result.completedDate,
                    ),
                hasLoadedContent = true,
                isLoading = false,
            )
        }
    }

    private fun clearCalendarOnError(date: YearMonth) {
        val currentDate = date.atDay(1)
        reduce {
            copy(
                detail =
                    detail.copy(
                        currentDate = currentDate,
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

    private fun fetchStatsDetail(date: YearMonth) {
        if (checkCache(date)) return
        launchResult(
            block = { statsRepository.fetchStatsDetail(argGoalId, date) },
            onSuccess = { reduceStatsDetail(it) },
            onError = {
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
                            currentDate = it.currentDate,
                            completedDate = it.completedDate,
                        ),
                )
            }
            return true
        }
        return false
    }

    private fun collectEventBus() {
        viewModelScope.launch {
            statsDetailRefreshBus.events.collect {
                refreshCurrentMonthStats()
            }
        }
    }

    private fun refreshCurrentMonthStats() {
        val yearMonth = YearMonth.from(currentState.detail.currentDate)
        viewModelScope.launch {
            val (summary, detail) = fetchStats(yearMonth)
            if (summary is AppResult.Success && detail is AppResult.Success) {
                cache.remove(yearMonth)
                reduce { copy(summary = summary.data) }
                reduceStatsDetail(detail.data)
            } else {
                if (summary is AppResult.Error) handleError(summary.error)
                if (detail is AppResult.Error) handleError(detail.error)
                showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
            }
        }
    }

    override suspend fun handleIntent(intent: StatsDetailIntent) {
        when (intent) {
            StatsDetailIntent.Retry -> fetchInitialStats()
            is StatsDetailIntent.SelectDate -> navigateToPhotologDetail(intent.date)
            StatsDetailIntent.GoalEdit -> navigateToGoalEditor()
            StatsDetailIntent.PreviousMonth -> fetchPreviousMonth()
            StatsDetailIntent.NextMonth -> fetchNextMonth()
            StatsDetailIntent.GoalEnd -> endGoal()
            StatsDetailIntent.GoalDelete -> deleteGoal()
        }
    }

    private suspend fun navigateToPhotologDetail(selectedDate: LocalDate) {
        val completedDate = findCompletedDate(selectedDate) ?: return
        if (completedDate.myImageUrl == null && completedDate.partnerImageUrl == null) return

        emitSideEffect(
            StatsDetailSideEffect.NavigateToPhotologDetail(
                goalId = argGoalId,
                date = selectedDate,
                betweenUs = determineDisplayBetweenUs(completedDate.date),
                isCompleted = currentState.detail.isCompleted,
            ),
        )
    }

    private suspend fun navigateToGoalEditor() {
        emitSideEffect(StatsDetailSideEffect.NavigateToGoalEditor(argGoalId))
    }

    private fun fetchPreviousMonth() {
        val previousMonth = currentState.detail.currentDate.minusMonths(1)
        monthChangeFlow.tryEmit(YearMonth.from(previousMonth))
    }

    private fun fetchNextMonth() {
        val nextMonth = currentState.detail.currentDate.plusMonths(1)
        monthChangeFlow.tryEmit(YearMonth.from(nextMonth))
    }

    private fun endGoal() {
        launchResult(
            block = { goalRepository.completeGoal(argGoalId) },
            onSuccess = {
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
                goalRefreshBus.notifyGoalListChanged()
                tryEmitSideEffect(StatsDetailSideEffect.NavigateToBack)
            },
            onError = { showToast(R.string.toast_complete_goal_failed, ToastType.ERROR) },
        )
    }

    private fun deleteGoal() {
        launchResult(
            block = { goalRepository.deleteGoal(argGoalId) },
            onSuccess = {
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
                goalRefreshBus.notifyGoalListChanged()
                tryEmitSideEffect(StatsDetailSideEffect.NavigateToBack)
            },
            onError = { showToast(R.string.toast_delete_goal_failed, ToastType.ERROR) },
        )
    }

    private fun determineDisplayBetweenUs(selectedDate: LocalDate): BetweenUs {
        val completedDate = findCompletedDate(selectedDate)

        return when {
            completedDate?.myImageUrl != null && completedDate.partnerImageUrl == null -> BetweenUs.ME
            else -> BetweenUs.PARTNER
        }
    }

    private fun findCompletedDate(selectedDate: LocalDate): CompletedDate? =
        currentState.detail.completedDate.firstOrNull { completed -> completed.date == selectedDate }

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
