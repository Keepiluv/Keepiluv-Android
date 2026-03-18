package com.twix.photolog.editor

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.photolog.editor.contract.PhotologEditorIntent
import com.twix.photolog.editor.contract.PhotologEditorSideEffect
import com.twix.photolog.editor.contract.PhotologEditorUiState
import com.twix.photolog.editor.contract.toEditorUiState
import com.twix.result.AppResult
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import kotlinx.coroutines.launch
import java.time.LocalDate

class PhotologEditorViewModel(
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: PhotologRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhotologEditorUiState, PhotologEditorIntent, PhotologEditorSideEffect>(
        PhotologEditorUiState(),
    ) {
    private val argGoalId: Long =
        requireNotNull(savedStateHandle[NavRoutes.PhotologEditorRoute.ARG_GOAL_ID]) { GOAL_ID_NOT_FOUND }

    private val argTargetDate: LocalDate =
        LocalDate.parse(
            requireNotNull(savedStateHandle[NavRoutes.PhotologEditorRoute.ARG_DATE]) { TARGET_DATE_NOT_FOUND },
        )

    init {
        fetchPhotolog()
    }

    override suspend fun handleIntent(intent: PhotologEditorIntent) {
        when (intent) {
            is PhotologEditorIntent.CommentFocusChanged -> reduceCommentFocus(intent.isFocused)
            is PhotologEditorIntent.ModifyComment -> reduceComment(intent.value)
            PhotologEditorIntent.Save -> modifyComment()
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
            showToast(R.string.toast_comment_length_guide, ToastType.ERROR)
        } else if (currentState.isCommentNotChanged) {
            showToast(R.string.toast_comment_not_modified, ToastType.ERROR)
        } else {
            launchResult(
                block = { launchModifyComment() },
                onSuccess = {
                    detailRefreshBus.notifyChanged(PhotologRefreshBus.Publisher.EDITOR)
                    goalRefreshBus.notifyGoalListChanged()
                    showToast(R.string.toast_comment_modify_success, ToastType.SUCCESS)
                },
                onError = {
                    showToast(R.string.toast_comment_modify_fail, ToastType.ERROR)
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
                PhotologEditorSideEffect.ShowToast(message, type),
            )
        }
    }

    private fun fetchPhotolog() {
        launchResult(
            block = { photologRepository.fetchPhotologs(argTargetDate, argGoalId) },
            onSuccess = { reduce { it.toEditorUiState(argGoalId, argTargetDate) } },
            onError = {
                showToast(R.string.toast_photolog_detail_fetch_fail, ToastType.ERROR)
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
