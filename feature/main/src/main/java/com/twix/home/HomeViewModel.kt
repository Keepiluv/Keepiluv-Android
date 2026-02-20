package com.twix.home

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.GoalCheckState
import com.twix.domain.model.enums.WeekNavigation
import com.twix.domain.repository.GoalRepository
import com.twix.home.model.CalendarState
import com.twix.home.model.HomeUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

class HomeViewModel(
    private val goalRepository: GoalRepository,
    private val goalRefreshBus: GoalRefreshBus,
) : BaseViewModel<HomeUiState, HomeIntent, HomeSideEffect>(
        HomeUiState(),
    ) {
    init {
        fetchGoalList()

        viewModelScope.launch {
            goalRefreshBus.goalEvents.collect {
                fetchGoalList()
            }
        }
    }

    val calendarState: StateFlow<CalendarState> =
        uiState
            .map { state ->
                CalendarState(
                    visibleDate = state.visibleDate,
                    selectedDate = state.selectedDate,
                    monthYearText = state.monthYear,
                )
            }.distinctUntilChanged()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue =
                    CalendarState(
                        visibleDate = currentState.visibleDate,
                        selectedDate = currentState.selectedDate,
                        monthYearText = currentState.monthYear,
                    ),
            )

    override suspend fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.SelectDate -> updateDate(intent.date)
            HomeIntent.NextWeek -> shiftWeek(WeekNavigation.NEXT)
            HomeIntent.PreviousWeek -> shiftWeek(WeekNavigation.PREVIOUS)
            HomeIntent.MoveToToday -> shiftWeek(WeekNavigation.TODAY)
            is HomeIntent.UpdateVisibleDate -> updateVisibleDate(intent.date)
            is HomeIntent.Verification -> handleGoalVerification(intent)
        }
    }

    private fun updateDate(date: LocalDate) {
        if (currentState.selectedDate == date) return

        if (date.month != currentState.visibleDate.month) updateVisibleDate(date)

        reduce { copy(selectedDate = date, referenceDate = date) }

        fetchGoalList()
    }

    private fun shiftWeek(action: WeekNavigation) {
        val newReference =
            when (action) {
                WeekNavigation.NEXT -> currentState.referenceDate.plusWeeks(1)
                WeekNavigation.PREVIOUS -> currentState.referenceDate.minusWeeks(1)
                WeekNavigation.TODAY -> LocalDate.now()
            }
        if (currentState.referenceDate == newReference) return
        if (action == WeekNavigation.TODAY) {
            updateDate(newReference)
            return
        }
        reduce { copy(referenceDate = newReference) }
    }

    private fun updateVisibleDate(date: LocalDate) {
        if (currentState.visibleDate.month == date.month) return

        reduce { copy(visibleDate = date) }
    }

    private fun handleGoalVerification(intent: HomeIntent.Verification) {
        viewModelScope.launch {
            when (intent.goalCheckState) {
                GoalCheckState.ONLY_ME,
                GoalCheckState.BOTH,
                -> {
                    emitSideEffect(
                        HomeSideEffect.ShowToast(
                            R.string.toast_already_certificated,
                            ToastType.SUCCESS,
                        ),
                    )
                }

                GoalCheckState.ONLY_PARTNER,
                GoalCheckState.NONE,
                -> {
                    reduce { copy(selectedGoalId = intent.goalId) }

                    emitSideEffect(HomeSideEffect.ShowPermissionLauncher)
                }
            }
        }
    }

    /**
     * 서버에서 데이터를 가져오는 부분
     * */
    private fun fetchGoalList() {
        val date = currentState.selectedDate.toString()

        launchResult(
            block = { goalRepository.fetchGoalList(date = date) },
            onSuccess = { goalList -> reduce { copy(goalList = goalList) } },
            onError = {
                emitSideEffect(
                    HomeSideEffect.ShowToast(
                        R.string.toast_goal_fetch_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }
}
