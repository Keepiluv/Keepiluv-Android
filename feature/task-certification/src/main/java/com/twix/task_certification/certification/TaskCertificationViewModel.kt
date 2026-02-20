package com.twix.task_certification.certification

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.navigation.serializer.DetailSerializer
import com.twix.task_certification.R
import com.twix.task_certification.certification.model.CaptureStatus
import com.twix.task_certification.certification.model.TaskCertificationIntent
import com.twix.task_certification.certification.model.TaskCertificationSideEffect
import com.twix.task_certification.certification.model.TaskCertificationUiState
import com.twix.ui.base.BaseViewModel
import com.twix.ui.image.ImageGenerator
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

class TaskCertificationViewModel(
    private val imageGenerator: ImageGenerator,
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: TaskCertificationRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationUiState, TaskCertificationIntent, TaskCertificationSideEffect>(
        TaskCertificationUiState(),
    ) {
    private val serializer: DetailSerializer =
        requireNotNull(
            savedStateHandle
                .get<String>(NavRoutes.TaskCertificationRoute.ARG_DATA)
                ?.let { encoded ->
                    val json = Uri.decode(encoded)
                    Json.decodeFromString<DetailSerializer>(json)
                },
        ) { SERIALIZER_NOT_FOUND }

    init {
        if (serializer.from == NavRoutes.TaskCertificationRoute.From.EDITOR) {
            reduceComment(serializer.comment)
        }
    }

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
        if (!currentState.comment.canUpload) {
            showValidationError()
            return
        }

        viewModelScope.launch {
            val imageBytes =
                withContext(Dispatchers.IO) {
                    imageGenerator.uriToByteArray(capture.uri)
                }
            if (imageBytes != null) {
                upload(imageBytes)
            } else {
                showToast(R.string.task_certification_image_translate_fail, ToastType.ERROR)
            }
        }
    }

    private fun showValidationError() {
        viewModelScope.launch {
            if (!currentState.comment.canUpload) {
                reduce { showCommentError() }
                delay(ERROR_DISPLAY_DURATION_MS)
                reduce { hideCommentError() }
            }
        }
    }

    private fun upload(image: ByteArray) {
        launchResult(
            block = {
                photologRepository.uploadPhotologImage(
                    goalId = serializer.goalId,
                    bytes = image,
                    contentType = "image/jpeg",
                )
            },
            onSuccess = { fileName ->
                when (serializer.from) {
                    NavRoutes.TaskCertificationRoute.From.DETAIL,
                    NavRoutes.TaskCertificationRoute.From.HOME,
                    -> uploadPhotolog(fileName)

                    NavRoutes.TaskCertificationRoute.From.EDITOR -> modifyPhotolog(fileName)
                }
            },
            onError = {
                showToast(R.string.task_certification_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun uploadPhotolog(fileName: String) {
        launchResult(
            block = {
                photologRepository.uploadPhotolog(
                    PhotologParam(
                        goalId = serializer.goalId,
                        fileName = fileName,
                        comment = currentState.comment.value,
                        verificationDate = LocalDate.parse(serializer.selectedDate),
                    ),
                )
            },
            onSuccess = { handleUploadPhotologSuccess() },
            onError = {
                showToast(R.string.task_certification_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun handleUploadPhotologSuccess() {
        when (serializer.from) {
            NavRoutes.TaskCertificationRoute.From.HOME ->
                goalRefreshBus.notifyGoalListChanged()
            NavRoutes.TaskCertificationRoute.From.DETAIL ->
                detailRefreshBus.notifyChanged(TaskCertificationRefreshBus.Publisher.PHOTOLOG)
            NavRoutes.TaskCertificationRoute.From.EDITOR -> Unit
        }
        tryEmitSideEffect(TaskCertificationSideEffect.NavigateToBack)
    }

    private fun modifyPhotolog(fileName: String) {
        launchResult(
            block = {
                photologRepository.modifyPhotolog(
                    photologId = serializer.photologId,
                    fileName = fileName,
                    comment = currentState.comment.value,
                )
            },
            onSuccess = {
                detailRefreshBus.notifyChanged(TaskCertificationRefreshBus.Publisher.PHOTOLOG)
                tryEmitSideEffect(TaskCertificationSideEffect.NavigateToDetail)
            },
            onError = {
                showToast(R.string.task_certification_upload_fail, ToastType.ERROR)
                showToast(R.string.task_certification_modify_fail, ToastType.ERROR)
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
        private const val SERIALIZER_NOT_FOUND = "Serializer Not Found"
    }
}
