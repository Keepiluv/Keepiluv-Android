package com.twix.task_certification.certification

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.task_certification.R
import com.twix.task_certification.certification.model.CaptureStatus
import com.twix.task_certification.certification.model.TaskCertificationIntent
import com.twix.task_certification.certification.model.TaskCertificationSideEffect
import com.twix.task_certification.certification.model.TaskCertificationUiState
import com.twix.ui.base.BaseViewModel
import com.twix.ui.image.ImageGenerator
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDate

class TaskCertificationViewModel(
    private val imageGenerator: ImageGenerator,
    private val photologRepository: PhotoLogRepository,
    private val taskCertificationRefreshBus: TaskCertificationRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    saveStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationUiState, TaskCertificationIntent, TaskCertificationSideEffect>(
        TaskCertificationUiState(),
    ) {
    private val goalId: Long =
        saveStateHandle[NavRoutes.TaskCertificationRoute.ARG_GOAL_ID]
            ?: error(GOAL_ID_NOT_FOUND)

    private val from: String =
        saveStateHandle[NavRoutes.TaskCertificationRoute.ARG_FROM]
            ?: error(FROM_NOT_FOUND)

    override suspend fun handleIntent(intent: TaskCertificationIntent) {
        when (intent) {
            is TaskCertificationIntent.TakePicture -> takePicture(intent.uri)
            is TaskCertificationIntent.PickPicture -> pickPicture(intent.uri)
            is TaskCertificationIntent.ToggleLens -> reduceLens()
            is TaskCertificationIntent.ToggleTorch -> reduceTorch()
            is TaskCertificationIntent.RetakePicture -> setupRetake()
            is TaskCertificationIntent.UpdateComment -> reduceComment(intent.value)
            is TaskCertificationIntent.CommentFocusChanged -> reduceCommentFocus(intent.isFocused)
            is TaskCertificationIntent.TryUpload -> handleUploadIntent()
            is TaskCertificationIntent.Upload -> upload(intent.image)
        }
    }

    private fun takePicture(uri: Uri?) {
        uri?.let { reducePicture(it) } ?: showToast(
            R.string.task_certification_image_capture_fail,
            ToastType.ERROR,
        )
    }

    private fun pickPicture(uri: Uri?) {
        uri?.let { reducePicture(uri) }
    }

    private fun reducePicture(uri: Uri) {
        reduce { updatePicture(uri) }
        if (uiState.value.hasMaxCommentLength.not()) {
            reduceCommentFocus(true)
        }
    }

    private fun reduceLens() {
        reduce { toggleLens() }
    }

    private fun reduceTorch() {
        reduce { toggleTorch() }
    }

    private fun setupRetake() {
        reduce { removePicture() }
    }

    private fun reduceComment(comment: String) {
        reduce { updateComment(comment) }
    }

    private fun reduceCommentFocus(isFocused: Boolean) {
        reduce { updateCommentFocus(isFocused) }
    }

    private fun handleUploadIntent() {
        val capture = currentState.capture as? CaptureStatus.Captured ?: return
        if (!currentState.commentUiModel.canUpload) {
            showValidationError()
            return
        }

        viewModelScope.launch {
            val imageBytes = imageGenerator.uriToByteArray(capture.uri)
            if (imageBytes != null) {
                upload(imageBytes)
            } else {
                showToast(R.string.task_certification_image_translate_fail, ToastType.ERROR)
            }
        }
    }

    private fun showValidationError() {
        viewModelScope.launch {
            if (!currentState.commentUiModel.canUpload) {
                reduce { showCommentError() }
                delay(ERROR_DISPLAY_DURATION_MS)
                reduce { hideCommentError() }
            }
        }
    }

    private fun upload(image: ByteArray) {
        launchResult(
            block = {
                photologRepository.uploadPhotoLogImage(
                    goalId = goalId,
                    bytes = image,
                    contentType = "image/jpeg",
                )
            },
            onSuccess = { fileName -> uploadPhotoLog(fileName) },
            onError = {
                showToast(R.string.task_certification_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun uploadPhotoLog(fileName: String) {
        launchResult(
            block = {
                photologRepository.uploadPhotoLog(
                    PhotologParam(
                        goalId = goalId,
                        fileName = fileName,
                        comment = currentState.commentUiModel.comment,
                        verificationDate = LocalDate.now(),
                    ),
                )
            },
            onSuccess = {
                when (NavRoutes.TaskCertificationRoute.From.valueOf(from)) {
                    NavRoutes.TaskCertificationRoute.From.DETAIL -> taskCertificationRefreshBus.notifyChanged()
                    NavRoutes.TaskCertificationRoute.From.HOME -> goalRefreshBus.notifyGoalListChanged()
                }
                tryEmitSideEffect(TaskCertificationSideEffect.NavigateToDetail)
            },
            onError = {
                showToast(R.string.task_certification_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(TaskCertificationSideEffect.ShowToast(message, type))
        }
    }

    companion object {
        private const val ERROR_DISPLAY_DURATION_MS = 1500L
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val FROM_NOT_FOUND = "From Argument Not Found"
    }
}
