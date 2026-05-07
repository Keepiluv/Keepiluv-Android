package com.twix.photolog.capture

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.DetailNavArgs
import com.twix.navigation.savedstate.decodeNavArgs
import com.twix.photolog.capture.contract.PhotologCaptureIntent
import com.twix.photolog.capture.contract.PhotologCaptureSideEffect
import com.twix.photolog.capture.contract.PhotologCaptureUiState
import com.twix.photolog.capture.model.CaptureStatus
import com.twix.photolog.capture.model.TorchStatus
import com.twix.ui.base.BaseViewModel
import com.twix.ui.image.ImageGenerator
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import com.twix.util.bus.StatsDetailRefreshBus
import com.twix.util.bus.StatsRefreshBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

class PhotologCaptureViewModel(
    private val imageGenerator: ImageGenerator,
    private val photologRepository: PhotoLogRepository,
    private val detailRefreshBus: PhotologRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    private val statsDetailRefreshBus: StatsDetailRefreshBus,
    private val statsRefreshBus: StatsRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhotologCaptureUiState, PhotologCaptureIntent, PhotologCaptureSideEffect>(
        PhotologCaptureUiState(),
    ) {
    private val navArgs: DetailNavArgs =
        savedStateHandle.decodeNavArgs<DetailNavArgs>(NavRoutes.PhotologRoute.ARG_DATA)

    init {
        if (navArgs.from == NavRoutes.PhotologRoute.From.EDITOR) {
            reduceComment(navArgs.comment)
        }
    }

    override suspend fun handleIntent(intent: PhotologCaptureIntent) {
        when (intent) {
            is PhotologCaptureIntent.TakePicture -> takePicture(intent.uri)
            is PhotologCaptureIntent.PickPicture -> pickPicture(intent.uri)
            is PhotologCaptureIntent.ToggleLens -> reduceLens()
            is PhotologCaptureIntent.ToggleTorch -> reduceTorch()
            is PhotologCaptureIntent.RetakePicture -> setupRetake()
            is PhotologCaptureIntent.UpdateComment -> reduceComment(intent.value)
            is PhotologCaptureIntent.CommentFocusChanged -> reduceCommentFocus(intent.isFocused)
            is PhotologCaptureIntent.TryUpload -> handleUploadIntent()
            is PhotologCaptureIntent.Upload -> upload(intent.image)
        }
    }

    private fun takePicture(uri: Uri?) {
        uri?.let { reducePicture(it) } ?: showToast(
            R.string.toast_image_capture_fail,
            ToastType.ERROR,
        )
    }

    private fun pickPicture(uri: Uri?) {
        uri?.let { reducePicture(uri) }
    }

    private fun reducePicture(uri: Uri) {
        reduce {
            copy(
                capture = CaptureStatus.Captured(uri),
            )
        }
        if (uiState.value.hasMaxCommentLength.not()) {
            reduceCommentFocus(true)
        }
    }

    private fun reduceLens() {
        val newLens =
            if (currentState.lens == CameraSelector.DEFAULT_BACK_CAMERA) {
                CameraSelector.DEFAULT_FRONT_CAMERA
            } else {
                CameraSelector.DEFAULT_BACK_CAMERA
            }

        reduce { copy(lens = newLens, torch = TorchStatus.Off) }
    }

    private fun reduceTorch() {
        reduce { copy(torch = TorchStatus.toggle(torch)) }
    }

    private fun setupRetake() {
        reduce { copy(capture = CaptureStatus.NotCaptured) }
    }

    private fun reduceComment(newComment: String) {
        reduce { copy(comment = comment.copy(value = newComment)) }
    }

    private fun reduceCommentFocus(isFocused: Boolean) {
        reduce { copy(comment = comment.copy(isFocused = isFocused)) }
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
                showToast(R.string.toast_image_translate_fail, ToastType.ERROR)
            }
        }
    }

    private fun showValidationError() {
        viewModelScope.launch {
            if (!currentState.comment.canUpload) {
                reduce { copy(showCommentError = true) }
                delay(ERROR_DISPLAY_DURATION_MS)
                reduce { copy(showCommentError = false) }
            }
        }
    }

    private fun upload(image: ByteArray) {
        launchResult(
            onStart = { reduce { copy(isLoading = true) } },
            block = {
                photologRepository.uploadPhotologImage(
                    goalId = navArgs.goalId,
                    bytes = image,
                    contentType = "image/jpeg",
                )
            },
            onSuccess = { fileName -> handleUploadPhotologSuccess(fileName) },
            onError = {
                reduce { copy(isLoading = false) }
                showToast(R.string.toast_photolog_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun handleUploadPhotologSuccess(fileName: String) {
        when (navArgs.from) {
            NavRoutes.PhotologRoute.From.DETAIL,
            NavRoutes.PhotologRoute.From.HOME,
            -> uploadPhotolog(fileName)

            NavRoutes.PhotologRoute.From.EDITOR -> modifyPhotolog(fileName)
        }
    }

    private fun uploadPhotolog(fileName: String) {
        launchResult(
            block = {
                photologRepository.uploadPhotolog(
                    PhotologParam(
                        goalId = navArgs.goalId,
                        fileName = fileName,
                        comment = currentState.comment.value,
                        verificationDate = LocalDate.parse(navArgs.selectedDate),
                    ),
                )
            },
            onSuccess = { handleUploadPhotologSuccess() },
            onError = {
                showToast(R.string.toast_photolog_upload_fail, ToastType.ERROR)
            },
        )
    }

    private fun handleUploadPhotologSuccess() {
        when (navArgs.from) {
            NavRoutes.PhotologRoute.From.HOME -> {
                goalRefreshBus.notifyGoalListChanged()
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
            }

            NavRoutes.PhotologRoute.From.DETAIL -> {
                detailRefreshBus.notifyChanged(PhotologRefreshBus.Publisher.PHOTOLOG)
                statsDetailRefreshBus.notifyChanged()
                statsRefreshBus.notifyChanged(StatsRefreshBus.Target.All)
            }

            NavRoutes.PhotologRoute.From.EDITOR -> Unit
        }
        tryEmitSideEffect(PhotologCaptureSideEffect.NavigateToBack)
    }

    private fun modifyPhotolog(fileName: String) {
        launchResult(
            block = {
                photologRepository.modifyPhotolog(
                    photologId = navArgs.photologId,
                    fileName = fileName,
                    comment = currentState.comment.value,
                )
            },
            onSuccess = { handleModifyPhotologSuccess() },
            onError = {
                showToast(R.string.toast_photolog_modify_fail, ToastType.ERROR)
            },
        )
    }

    private fun handleModifyPhotologSuccess() {
        detailRefreshBus.notifyChanged(PhotologRefreshBus.Publisher.PHOTOLOG)
        goalRefreshBus.notifyGoalListChanged()
        statsDetailRefreshBus.notifyChanged()
        val selectedDate = LocalDate.parse(navArgs.selectedDate)
        tryEmitSideEffect(
            PhotologCaptureSideEffect.NavigateToDetail(
                goalId = navArgs.goalId,
                date = selectedDate,
                betweenUs = BetweenUs.ME,
            ),
        )
    }

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(PhotologCaptureSideEffect.ShowToast(message, type))
        }
    }

    companion object {
        private const val ERROR_DISPLAY_DURATION_MS = 1500L
    }
}
