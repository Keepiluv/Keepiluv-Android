package com.twix.goal_manage

import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.WeekNavigation
import com.twix.domain.repository.GoalRepository
import com.twix.goal_manage.model.GoalDialogState
import com.twix.goal_manage.model.GoalManageUiState
import com.twix.goal_manage.model.RemovedGoal
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.StatsRefreshBus
import kotlinx.coroutines.launch
import java.time.LocalDate

class GoalManageViewModel(
    private val goalRepository: GoalRepository,
    private val goalRefreshBus: GoalRefreshBus,
    private val statsRefreshBus: StatsRefreshBus,
) : BaseViewModel<GoalManageUiState, GoalManageIntent, GoalManageSideEffect>(GoalManageUiState()) {
    init {
        viewModelScope.launch {
            goalRefreshBus.goalSummariesEvents.collect {
                refreshGoalSummaryList()
            }
        }
    }

    override suspend fun handleIntent(intent: GoalManageIntent) {
        when (intent) {
            GoalManageIntent.Retry ->
                fetchGoalSummaryList(
                    date = currentState.selectedDate,
                    showInitialLoading = true,
                    showToastOnError = false,
                )
            is GoalManageIntent.EndGoal -> endGoal(intent.id)
            is GoalManageIntent.DeleteGoal -> deleteGoal(intent.id)
            is GoalManageIntent.SetSelectedDate -> setSelectedDate(intent.date)
            GoalManageIntent.NextWeek -> shiftWeek(WeekNavigation.NEXT)
            GoalManageIntent.PreviousWeek -> shiftWeek(WeekNavigation.PREVIOUS)
            is GoalManageIntent.OpenMenu -> reduce { copy(openedMenuGoalId = intent.goalId) }
            GoalManageIntent.CloseMenu -> reduce { copy(openedMenuGoalId = null) }
            is GoalManageIntent.ShowEndDialog -> {
                if (intent.goalId in currentState.pendingGoalIds) return
                val goal = currentState.goalSummaries.firstOrNull { it.goalId == intent.goalId } ?: return
                reduce {
                    copy(
                        endDialog = GoalDialogState(goal.goalId, goal.name, goal.icon),
                        openedMenuGoalId = null,
                    )
                }
            }
            GoalManageIntent.DismissEndDialog -> reduce { copy(endDialog = null) }
            is GoalManageIntent.ShowDeleteDialog -> {
                if (intent.goalId in currentState.pendingGoalIds) return
                val goal = currentState.goalSummaries.firstOrNull { it.goalId == intent.goalId } ?: return
                reduce {
                    copy(
                        deleteDialog = GoalDialogState(goal.goalId, goal.name, goal.icon),
                        openedMenuGoalId = null,
                    )
                }
            }
            GoalManageIntent.DismissDeleteDialog -> reduce { copy(deleteDialog = null) }
            is GoalManageIntent.EditGoal -> {
                reduce { copy(openedMenuGoalId = null) }
                emitSideEffect(GoalManageSideEffect.NavigateToGoalEditor(intent.goalId))
            }
        }
    }

    private fun endGoal(id: Long) {
        if (id in currentState.pendingGoalIds) return

        val removed = removeGoalOptimistically(id) ?: return

        markPending(id, true)

        launchResult(
            block = { goalRepository.completeGoal(id) },
            onSuccess = {
                goalRefreshBus.notifyGoalListChanged()
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
                markPending(id, false)
            },
            onError = {
                markPending(id, false)
                restoreGoal(removed)
                emitSideEffect(GoalManageSideEffect.ShowToast(R.string.toast_complete_goal_failed, ToastType.ERROR))
            },
        )
    }

    private fun deleteGoal(id: Long) {
        if (id in currentState.pendingGoalIds) return

        val removed = removeGoalOptimistically(id) ?: return

        markPending(id, true)

        launchResult(
            block = { goalRepository.deleteGoal(id) },
            onSuccess = {
                goalRefreshBus.notifyGoalListChanged()
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
                markPending(id, false)
            },
            onError = {
                markPending(id, false)
                restoreGoal(removed)
                emitSideEffect(GoalManageSideEffect.ShowToast(R.string.toast_delete_goal_failed, ToastType.ERROR))
            },
        )
    }

    private fun removeGoalOptimistically(id: Long): RemovedGoal? {
        val index = currentState.goalSummaries.indexOfFirst { it.goalId == id }
        if (index == -1) return null
        val item = currentState.goalSummaries[index]

        reduce {
            copy(
                goalSummaries = goalSummaries.filterNot { it.goalId == id },
                openedMenuGoalId = if (openedMenuGoalId == id) null else openedMenuGoalId,
            )
        }
        return RemovedGoal(index, item)
    }

    private fun setSelectedDate(date: LocalDate) {
        if (currentState.selectedDate == date && currentState.isInitialized) return
        reduce { copy(selectedDate = date, isInitialized = true) }

        val shouldShowInitialLoading = !currentState.hasLoadedInitialData
        fetchGoalSummaryList(
            date = date,
            showInitialLoading = shouldShowInitialLoading,
            showToastOnError = !shouldShowInitialLoading,
        )
    }

    private fun refreshGoalSummaryList() {
        if (!currentState.hasLoadedInitialData) return

        fetchGoalSummaryList(
            date = currentState.selectedDate,
            showInitialLoading = false,
            showToastOnError = false,
        )
    }

    private fun fetchGoalSummaryList(
        date: LocalDate,
        showInitialLoading: Boolean,
        showToastOnError: Boolean,
    ) {
        launchResult(
            block = { goalRepository.fetchGoalSummaryList(date.toString()) },
            onSuccess = {
                reduce {
                    copy(
                        goalSummaries = it,
                        hasLoadedInitialData = true,
                    )
                }
            },
            onError = {
                if (!showInitialLoading && showToastOnError) {
                    emitSideEffect(GoalManageSideEffect.ShowToast(R.string.toast_goal_fetch_failed, ToastType.ERROR))
                }
            },
        )
    }

    private fun restoreGoal(removed: RemovedGoal) {
        reduce {
            if (goalSummaries.any { it.goalId == removed.item.goalId }) return@reduce this
            val list = goalSummaries.toMutableList()
            val safeIndex = removed.index.coerceIn(0, list.size)
            list.add(safeIndex, removed.item)
            copy(goalSummaries = list)
        }
    }

    private fun markPending(
        id: Long,
        pending: Boolean,
    ) {
        reduce {
            copy(
                pendingGoalIds = if (pending) pendingGoalIds + id else pendingGoalIds - id,
            )
        }
    }

    private fun shiftWeek(action: WeekNavigation) {
        val newReference =
            when (action) {
                WeekNavigation.NEXT -> currentState.referenceDate.plusWeeks(1)
                WeekNavigation.PREVIOUS -> currentState.referenceDate.minusWeeks(1)
                WeekNavigation.TODAY -> LocalDate.now()
            }
        if (currentState.referenceDate == newReference) return
        reduce { copy(referenceDate = newReference) }
    }
}
