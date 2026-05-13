package com.twix.photolog.editor.contract

import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.result.AppError
import com.twix.ui.base.LoadableState
import java.time.LocalDate

@Immutable
data class PhotologEditorUiState(
    val goalId: Long = -1,
    val photologId: Long = -1,
    val selectedDate: LocalDate = LocalDate.now(),
    val nickname: String = "",
    val goalName: String = "",
    val imageUrl: String = "",
    val comment: CommentUiModel = CommentUiModel(),
    val originComment: String = "",
    val isSaving: Boolean = false,
    override val isLoading: Boolean = true,
    override val error: AppError? = null,
) : LoadableState {
    val hasLoadedContent: Boolean
        get() = goalId != -1L || photologId != -1L || imageUrl.isNotEmpty()

    val showLoading: Boolean
        get() = isLoading && !hasLoadedContent

    val showError: Boolean
        get() = error != null && !hasLoadedContent

    val isCommentNotChanged: Boolean
        get() = comment.value == originComment

    val imageName: String
        get() = imageUrl.split(IMAGE_NAME_SEPARATOR).last()

    override fun copyLoadableState(
        isLoading: Boolean,
        error: AppError?,
    ): LoadableState =
        copy(
            isLoading = isLoading,
            error = error,
        )

    companion object {
        private const val IMAGE_NAME_SEPARATOR = "/"
    }
}

internal fun PhotoLogs.toEditorUiState(
    goalId: Long,
    selectedDate: LocalDate,
): PhotologEditorUiState {
    val goalPhotolog = goals.firstOrNull { it.goalId == goalId }
    val myPhotolog = goalPhotolog?.myPhotolog

    return PhotologEditorUiState(
        goalId = goalId,
        photologId = myPhotolog?.photologId ?: -1,
        selectedDate = selectedDate,
        nickname = myNickname,
        goalName = goalPhotolog?.goalName.orEmpty(),
        imageUrl = myPhotolog?.imageUrl.orEmpty(),
        comment = CommentUiModel(myPhotolog?.comment.orEmpty()),
        originComment = myPhotolog?.comment.orEmpty(),
        isLoading = false,
    )
}
