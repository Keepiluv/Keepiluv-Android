package com.twix.task_certification.editor

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.EditorNavArgs
import com.twix.result.AppResult
import com.twix.task_certification.R
import com.twix.task_certification.editor.model.TaskCertificationEditorIntent
import com.twix.task_certification.editor.model.TaskCertificationEditorSideEffect
import com.twix.task_certification.editor.model.TaskCertificationEditorUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class TaskCertificationEditorViewModel(
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: TaskCertificationRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationEditorUiState, TaskCertificationEditorIntent, TaskCertificationEditorSideEffect>(
        TaskCertificationEditorUiState(),
    ) {
    private val serializer =
        requireNotNull(
            savedStateHandle
                .get<String>(NavRoutes.TaskCertificationEditorRoute.ARG_DATA)
                ?.let { encoded ->
                    val json = Uri.decode(encoded)
                    Json.decodeFromString<EditorNavArgs>(json)
                },
        ) { SERIALIZER_NOT_FOUND }

    init {
        reduceInitialState()
    }

    private fun reduceInitialState() {
        reduce { updateInitialState(serializer) }
    }

    override suspend fun handleIntent(intent: TaskCertificationEditorIntent) {
        when (intent) {
            is TaskCertificationEditorIntent.CommentFocusChanged -> reduceCommentFocus(intent.isFocused)
            is TaskCertificationEditorIntent.ModifyComment -> reduceComment(intent.value)
            TaskCertificationEditorIntent.Save -> modifyComment()
        }
    }

    private fun reduceCommentFocus(isFocused: Boolean) {
        reduce { updateCommentFocus(isFocused) }
    }

    private fun reduceComment(comment: String) {
        reduce { updateComment(comment) }
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
