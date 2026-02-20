package com.twix.task_certification.certification.contract

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.task_certification.certification.model.CaptureStatus
import com.twix.task_certification.certification.model.TorchStatus
import com.twix.task_certification.certification.model.camera.CameraPreview
import com.twix.ui.base.State

@Immutable
data class TaskCertificationUiState(
    val capture: CaptureStatus = CaptureStatus.NotCaptured,
    val torch: TorchStatus = TorchStatus.Off,
    val lens: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val preview: CameraPreview? = null,
    val comment: CommentUiModel = CommentUiModel(),
    val showCommentError: Boolean = false,
) : State {
    val hasMaxCommentLength: Boolean
        get() = comment.hasMaxCommentLength

    val showTorch: Boolean
        get() = capture is CaptureStatus.NotCaptured && lens == CameraSelector.DEFAULT_BACK_CAMERA

    fun toggleLens(): TaskCertificationUiState {
        val newLens =
            if (lens == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }
        return copy(
            lens = newLens,
            torch = TorchStatus.Off,
        )
    }

    fun toggleTorch(): TaskCertificationUiState {
        val newFlashMode = TorchStatus.Companion.toggle(torch)
        return copy(torch = newFlashMode)
    }

    fun updatePicture(uri: Uri): TaskCertificationUiState =
        copy(
            capture = CaptureStatus.Captured(uri),
            torch = TorchStatus.Off,
        )

    fun removePicture(): TaskCertificationUiState = copy(capture = CaptureStatus.NotCaptured)

    fun updateComment(newComment: String) = copy(comment = comment.updateComment(newComment))

    fun updateCommentFocus(isFocused: Boolean) = copy(comment = comment.updateFocus(isFocused))

    fun showCommentError() = copy(showCommentError = true)

    fun hideCommentError() = copy(showCommentError = false)
}
