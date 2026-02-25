package com.twix.task_certification.editor.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.ui.base.State

@Immutable
data class TaskCertificationEditorUiState(
    val goalId: Long = -1,
    val photologId: Long = -1,
    val nickname: String = "",
    val goalName: String = "",
    val imageUrl: String = "",
    val comment: CommentUiModel = CommentUiModel(),
    val originComment: String = "",
) : State {
    val isCommentNotChanged: Boolean
        get() = comment.value == originComment

    val imageName: String
        get() = imageUrl.split(IMAGE_NAME_SEPARATOR).last()

    companion object {
        private const val IMAGE_NAME_SEPARATOR = "/"
    }
}

internal fun PhotoLogs.toEditorUiState(goalId: Long): TaskCertificationEditorUiState {
    val goalPhotolog = goals.firstOrNull { it.goalId == goalId }
    val myPhotolog = goalPhotolog?.myPhotolog

    return TaskCertificationEditorUiState(
        goalId = goalId,
        photologId = myPhotolog?.photologId ?: -1,
        nickname = myNickname,
        goalName = goalPhotolog?.goalName.orEmpty(),
        imageUrl = myPhotolog?.imageUrl.orEmpty(),
        comment = CommentUiModel(myPhotolog?.comment.orEmpty()),
        originComment = myPhotolog?.comment.orEmpty(),
    )
}
