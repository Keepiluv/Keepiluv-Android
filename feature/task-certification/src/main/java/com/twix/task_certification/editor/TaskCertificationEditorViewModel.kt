package com.twix.task_certification.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.result.AppResult
import com.twix.task_certification.editor.contract.TaskCertificationEditorIntent
import com.twix.task_certification.editor.contract.TaskCertificationEditorSideEffect
import com.twix.task_certification.editor.contract.TaskCertificationEditorUiState
import com.twix.task_certification.editor.contract.toEditorUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskCertificationEditorViewModel(
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: TaskCertificationRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationEditorUiState, TaskCertificationEditorIntent, TaskCertificationEditorSideEffect>(
        TaskCertificationEditorUiState(),
    ) {
    private val argGoalId: Long =
        requireNotNull(savedStateHandle[NavRoutes.TaskCertificationEditorRoute.ARG_GOAL_ID]) { GOAL_ID_NOT_FOUND }

    private val argTargetDate: LocalDate =
        LocalDate.parse(
            requireNotNull(savedStateHandle[NavRoutes.TaskCertificationEditorRoute.ARG_DATE]) { TARGET_DATE_NOT_FOUND },
        )

    init {
        fetchPhotolog()
    }

    override suspend fun handleIntent(intent: TaskCertificationEditorIntent) {
        when (intent) {
            is TaskCertificationEditorIntent.CommentFocusChanged -> reduceCommentFocus(intent.isFocused)
            is TaskCertificationEditorIntent.ModifyComment -> reduceComment(intent.value)
            TaskCertificationEditorIntent.Save -> modifyComment()
        }
    }

    private fun reduceCommentFocus(value: Boolean) {
        reduce { copy(comment = comment.copy(isFocused = value)) }
    }

    private fun reduceComment(value: String) {
        reduce { copy(comment = comment.copy(value = value)) }
    }

    private fun modifyComment() {
        if (currentState.comment.canUpload.not()) {
            showToast(R.string.comment_error_message, ToastType.ERROR)
        } else if (currentState.isCommentNotChanged) {
            showToast(R.string.task_certification_editor_not_modified, ToastType.ERROR)
        } else {
            launchResult(
                block = { launchModifyComment() },
                onSuccess = {
                    detailRefreshBus.notifyChanged(TaskCertificationRefreshBus.Publisher.EDITOR)
                    goalRefreshBus.notifyGoalListChanged()
                    showToast(R.string.task_certification_editor_modify_success, ToastType.SUCCESS)
                },
                onError = {
                    showToast(R.string.task_certification_editor_modify_fail, ToastType.ERROR)
                },
            )
        }
    }

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(
                TaskCertificationEditorSideEffect.ShowToast(message, type),
            )
        }
    }

    private fun fetchPhotolog() {
        launchResult(
            block = { photologRepository.fetchPhotologs(argTargetDate, argGoalId) },
            onSuccess = { reduce { it.toEditorUiState(argGoalId, argTargetDate) } },
            onError = {
                showToast(R.string.task_certification_detail_fetch_photolog_fail, ToastType.ERROR)
            },
        )
    }

    private suspend fun launchModifyComment(): AppResult<Unit> =
        photologRepository.modifyPhotolog(
            currentState.photologId,
            currentState.imageName,
            currentState.comment.value,
        )

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val TARGET_DATE_NOT_FOUND = "Target Date Argument Not Found"
    }
}
