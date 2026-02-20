package com.twix.task_certification.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.EditorNavArgs
import com.twix.navigation.savedstate.decodeNavArgs
import com.twix.result.AppResult
import com.twix.task_certification.editor.contract.TaskCertificationEditorIntent
import com.twix.task_certification.editor.contract.TaskCertificationEditorSideEffect
import com.twix.task_certification.editor.contract.TaskCertificationEditorUiState
import com.twix.task_certification.editor.contract.toUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.launch

class TaskCertificationEditorViewModel(
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: TaskCertificationRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationEditorUiState, TaskCertificationEditorIntent, TaskCertificationEditorSideEffect>(
        TaskCertificationEditorUiState(),
    ) {
    private val navArgs: EditorNavArgs =
        savedStateHandle.decodeNavArgs<EditorNavArgs>(NavRoutes.TaskCertificationEditorRoute.ARG_DATA)

    init {
        reduce { navArgs.toUiState() }
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

    private suspend fun launchModifyComment(): AppResult<Unit> =
        photologRepository.modifyPhotolog(
            currentState.photologId,
            currentState.imageName,
            currentState.comment.value,
        )

    companion object {
        private const val SERIALIZER_NOT_FOUND = "Serializer Not Found"
    }
}
