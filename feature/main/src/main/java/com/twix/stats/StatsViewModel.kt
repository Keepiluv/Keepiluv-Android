package com.twix.stats

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.StatsStatus
import com.twix.domain.model.stats.Stats
import com.twix.domain.repository.StatsRepository
import com.twix.stats.contract.StatsIntent
import com.twix.stats.contract.StatsSideEffect
import com.twix.stats.contract.StatsUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.StatsRefreshBus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.YearMonth

@OptIn(FlowPreview::class)
class StatsViewModel(
    private val statsRepository: StatsRepository,
    private val eventBus: StatsRefreshBus,
) : BaseViewModel<StatsUiState, StatsIntent, StatsSideEffect>(StatsUiState()) {
    private val inProgressStatsCache = mutableMapOf<YearMonth, Stats>()

    /**
     * 진행 중인 통계 조회의 최신 요청 식별자.
     *
     * 월 전환이 빠르게 연속으로 일어날 때 비동기 응답 역전으로
     * 이전 월 데이터가 현재 화면을 덮어쓰는 것을 방지하기 위해 사용한다.
     * 성공/실패 콜백에서 `requestId == latestInProgressRequestId`를 만족할 때만
     * 현재 UI 상태를 갱신한다.
     */
    private var latestInProgressRequestId = 0L

    private val monthChangeFlow =
        MutableSharedFlow<YearMonth>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    init {
        viewModelScope.launch {
            monthChangeFlow
                .distinctUntilChanged()
                .debounce(DEBOUNCE_INTERVAL)
                .collectLatest { yearMonth ->
                    fetchInProgressStats(yearMonth)
                }
        }

        fetchInProgressStats(YearMonth.from(currentState.currentDate))
        fetchCompletedStats()
        collectEventBus()
    }

    override suspend fun handleIntent(intent: StatsIntent) {
        when (intent) {
            is StatsIntent.PreviousMonth -> fetchPreviousMonthStats()
            is StatsIntent.NextMonth -> fetchNextMonthStats()
        }
    }

    private fun fetchInProgressStats(
        date: YearMonth,
        refresh: Boolean = false,
    ) {
        if (!refresh && applyCached(date)) return
        val requestId = ++latestInProgressRequestId

        launchResult(
            block = { statsRepository.fetchStats(date, StatsStatus.IN_PROGRESS) },
            onSuccess = { stats -> handleFetchInProgressStatsSuccess(stats, date, requestId) },
            onError = { handleFetchInProgressStatsFail(requestId, date) },
        )
    }

    private fun handleFetchInProgressStatsSuccess(
        stats: Stats,
        date: YearMonth,
        requestId: Long,
    ) {
        inProgressStatsCache[date] = stats

        val isLatestRequest = requestId == latestInProgressRequestId
        val isCurrentMonth = YearMonth.from(currentState.currentDate) == date
        if (isLatestRequest && isCurrentMonth) {
            reduce { copy(inProgressStats = stats) }
        }
    }

    private suspend fun handleFetchInProgressStatsFail(
        requestId: Long,
        date: YearMonth,
    ) {
        val isLatestRequest = requestId == latestInProgressRequestId
        val isCurrentMonth = YearMonth.from(currentState.currentDate) == date
        if (isLatestRequest && isCurrentMonth) {
            showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR)
        }
    }

    private fun fetchPreviousMonthStats() {
        val previousMonth = currentState.currentDate.minusMonths(1)
        reduce { copy(currentDate = previousMonth) }
        val yearMonth = YearMonth.from(previousMonth)
        if (applyCached(yearMonth)) return
        monthChangeFlow.tryEmit(yearMonth)
    }

    private fun fetchNextMonthStats() {
        val nextMonth = currentState.currentDate.plusMonths(1)
        reduce { copy(currentDate = nextMonth) }
        val yearMonth = YearMonth.from(nextMonth)
        if (applyCached(yearMonth)) return
        monthChangeFlow.tryEmit(yearMonth)
    }

    private fun fetchCompletedStats() {
        launchResult(
            block = {
                statsRepository.fetchStats(
                    YearMonth.from(currentState.currentDate),
                    StatsStatus.COMPLETED,
                )
            },
            onSuccess = { reduce { copy(completedStats = it.statsGoals) } },
            onError = { showToast(R.string.toast_fetch_stats_failed, ToastType.ERROR) },
        )
    }

    private fun collectEventBus() {
        viewModelScope.launch {
            eventBus.events.collect { publisher ->
                when (publisher) {
                    StatsRefreshBus.Publisher.InProgress -> refreshInProgressStats()
                    StatsRefreshBus.Publisher.End -> fetchCompletedStats()
                }
            }
        }
    }

    private fun refreshInProgressStats() {
        inProgressStatsCache.remove(YearMonth.from(currentState.currentDate))
        fetchInProgressStats(
            YearMonth.from(currentState.currentDate),
            refresh = true,
        )
    }

    private fun applyCached(yearMonth: YearMonth): Boolean {
        val cached = inProgressStatsCache[yearMonth] ?: return false
        reduce { copy(inProgressStats = cached) }
        return true
    }

    private suspend fun showToast(
        message: Int,
        type: ToastType,
    ) {
        emitSideEffect(StatsSideEffect.ShowToast(message, type))
    }

    companion object {
        private const val DEBOUNCE_INTERVAL = 300L
    }
}
