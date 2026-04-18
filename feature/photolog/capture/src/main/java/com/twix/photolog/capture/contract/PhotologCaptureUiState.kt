package com.twix.photolog.capture.contract

import androidx.camera.core.CameraSelector
import androidx.compose.runtime.Immutable
import com.twix.designsystem.components.comment.model.CommentUiModel
import com.twix.photolog.capture.model.CaptureStatus
import com.twix.photolog.capture.model.TorchStatus
import com.twix.photolog.capture.model.camera.CameraPreview
import com.twix.ui.base.State

@Immutable
data class PhotologCaptureUiState(
    val capture: CaptureStatus = CaptureStatus.NotCaptured,
    val torch: TorchStatus = TorchStatus.Off,
    val lens: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA,
    val preview: CameraPreview? = null,
    val comment: CommentUiModel = CommentUiModel(),
    val showCommentError: Boolean = false,
    val isLoading: Boolean = false,
) : State {
    val hasMaxCommentLength: Boolean
        get() = comment.hasMaxCommentLength

    val showTorch: Boolean
        get() = capture is CaptureStatus.NotCaptured && lens == CameraSelector.DEFAULT_BACK_CAMERA
}
