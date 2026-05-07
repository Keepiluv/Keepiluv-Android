package com.twix.goal_editor

import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.goal.CreateGoalParam
import com.twix.domain.model.goal.GoalDetail
import com.twix.domain.model.goal.UpdateGoalParam
import com.twix.domain.repository.GoalRepository
import com.twix.goal_editor.model.GoalEditorUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.StatsDetailRefreshBus
import com.twix.util.bus.StatsRefreshBus
import java.time.LocalDate

class GoalEditorViewModel(
    private val goalRepository: GoalRepository,
    private val goalRefreshBus: GoalRefreshBus,
    private val statsDetailRefreshBus: StatsDetailRefreshBus,
    private val statsRefreshBus: StatsRefreshBus,
) : BaseViewModel<GoalEditorUiState, GoalEditorIntent, GoalEditorSideEffect>(
        GoalEditorUiState(),
    ) {
    private var initializedGoalId: Long? = null
    private var initializedPresetKey: String? = null

    override suspend fun handleIntent(intent: GoalEditorIntent) {
        when (intent) {
            is GoalEditorIntent.Save -> save(intent.id)
            is GoalEditorIntent.SetIcon -> setIcon(intent.icon)
            is GoalEditorIntent.SetEndDate -> setEndDate(intent.endDate)
            is GoalEditorIntent.SetRepeatCount -> setRepeatCount(intent.repeatCount)
            is GoalEditorIntent.SetRepeatType -> setRepeatType(intent.repeatCycle)
            is GoalEditorIntent.SetStartDate -> setStartDate(intent.startDate)
            is GoalEditorIntent.SetTitle -> setTitle(intent.title)
            is GoalEditorIntent.SetEndDateEnabled -> setEndDateEnabled(intent.enabled)
            is GoalEditorIntent.InitGoal -> initGoal(intent.id)
            is GoalEditorIntent.InitPreset ->
                initPreset(
                    title = intent.title,
                    icon = intent.icon,
                    repeatCycle = intent.repeatCycle,
                    repeatCount = intent.repeatCount,
                )
        }
    }

    private fun setIcon(icon: GoalIconType) {
        reduce { copy(selectedIcon = icon) }
    }

    private fun setTitle(title: String) {
        if (title.isBlank()) return

        reduce { copy(goalTitle = title) }
    }

    private fun setRepeatType(repeatCycle: RepeatCycle) {
        reduce { copy(selectedRepeatCycle = repeatCycle, repeatCount = 1) }
    }

    private fun setRepeatCount(repeatCount: Int) {
        if (repeatCount <= 0) return

        reduce { copy(repeatCount = repeatCount) }
    }

    private fun setStartDate(startDate: LocalDate) {
        reduce {
            val validStartDate = startDate.validStartDate()
            copy(
                startDate = validStartDate,
                endDate = endDate.validEndDate(validStartDate),
            )
        }
    }

    private fun setEndDate(endDate: LocalDate) {
        reduce {
            copy(endDate = endDate.validEndDate(startDate))
        }
    }

    private fun setEndDateEnabled(enabled: Boolean) {
        reduce {
            copy(
                endDateEnabled = enabled,
                endDate = if (enabled) endDate.validEndDate(startDate) else endDate,
            )
        }
    }

    private fun setGoal(goal: GoalDetail) {
        reduce {
            copy(
                goalTitle = goal.name,
                selectedIcon = goal.icon,
                selectedRepeatCycle = goal.repeatCycle,
                repeatCount = goal.repeatCount,
                startDate = goal.startDate.validStartDate(),
                endDate = (goal.endDate ?: LocalDate.now()).validEndDate(goal.startDate.validStartDate()),
                endDateEnabled = goal.endDate != null,
            )
        }
    }

    private suspend fun save(id: Long) {
        if (currentState.isSaving) return
        if (!validateSaveInput()) return

        reduce { copy(isSaving = true) }

        if (id == -1L) createGoal() else updateGoal(id)
    }

    private suspend fun validateSaveInput(): Boolean {
        if (!currentState.isSaveEnabled) {
            emitSideEffect(
                GoalEditorSideEffect.ShowToast(
                    R.string.toast_input_goal_title,
                    ToastType.ERROR,
                ),
            )
            return false
        }

        if (!currentState.isEndDateValid) {
            emitSideEffect(
                GoalEditorSideEffect.ShowToast(
                    R.string.toast_end_date_before_start_date,
                    ToastType.ERROR,
                ),
            )
            return false
        }

        return true
    }

    private fun createGoal() {
        launchResult(
            block = { goalRepository.createGoal(currentState.toCreateParam()) },
            onFinally = { reduce { copy(isSaving = false) } },
            onSuccess = { onGoalSaveSuccess(isUpdate = false) },
            onError = {
                emitSideEffect(
                    GoalEditorSideEffect.ShowToast(
                        R.string.toast_create_goal_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun updateGoal(id: Long) {
        launchResult(
            block = { goalRepository.updateGoal(currentState.toUpdateParam(id)) },
            onFinally = { reduce { copy(isSaving = false) } },
            onSuccess = { onGoalSaveSuccess(isUpdate = true) },
            onError = {
                emitSideEffect(
                    GoalEditorSideEffect.ShowToast(
                        R.string.toast_update_goal_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun onGoalSaveSuccess(isUpdate: Boolean) {
        goalRefreshBus.notifyGoalListChanged()
        statsDetailRefreshBus.notifyChanged()
        statsRefreshBus.notifyChanged(StatsRefreshBus.Target.InProgress)

        if (isUpdate) {
            goalRefreshBus.notifyGoalSummariesChanged()
        }

        tryEmitSideEffect(GoalEditorSideEffect.NavigateToHome)
    }

    private fun initGoal(id: Long) {
        if (initializedGoalId == id) return
        initializedGoalId = id

        launchResult(
            block = { goalRepository.fetchGoalDetail(id) },
            onSuccess = { setGoal(it) },
            onError = {
                initializedGoalId = null
                emitSideEffect(
                    GoalEditorSideEffect.ShowToast(
                        R.string.toast_goal_fetch_failed,
                        ToastType.ERROR,
                    ),
                )
            },
        )
    }

    private fun initPreset(
        title: String,
        icon: GoalIconType,
        repeatCycle: RepeatCycle,
        repeatCount: Int,
    ) {
        val presetKey = "$title|$icon|$repeatCycle|$repeatCount"
        if (initializedPresetKey == presetKey) return
        initializedPresetKey = presetKey

        reduce {
            copy(
                goalTitle = title,
                selectedIcon = icon,
                selectedRepeatCycle = repeatCycle,
                repeatCount = repeatCount,
            )
        }
    }

    private fun GoalEditorUiState.toCreateParam(): CreateGoalParam =
        CreateGoalParam(
            name = goalTitle.trim(),
            icon = selectedIcon,
            repeatCycle = selectedRepeatCycle,
            repeatCount = repeatCount,
            startDate = startDate,
            endDate = if (endDateEnabled) endDate else null,
        )

    private fun GoalEditorUiState.toUpdateParam(id: Long): UpdateGoalParam =
        UpdateGoalParam(
            goalId = id,
            name = goalTitle.trim(),
            icon = selectedIcon,
            repeatCycle = selectedRepeatCycle,
            repeatCount = repeatCount,
            endDate = if (endDateEnabled) endDate else null,
        )

    private fun LocalDate.validStartDate(): LocalDate = maxOf(this, LocalDate.now())

    private fun LocalDate.validEndDate(startDate: LocalDate): LocalDate = maxOf(this, LocalDate.now(), startDate)
}
