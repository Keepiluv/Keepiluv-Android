package com.twix.task_certification.certification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.comment.CommentAnchorFrame
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.BetweenUs
import com.twix.task_certification.certification.component.CameraControlBar
import com.twix.task_certification.certification.component.CameraPreviewBox
import com.twix.task_certification.certification.component.CommentErrorText
import com.twix.task_certification.certification.component.LoadingContent
import com.twix.task_certification.certification.component.TaskCertificationTopBar
import com.twix.task_certification.certification.contract.TaskCertificationIntent
import com.twix.task_certification.certification.contract.TaskCertificationSideEffect
import com.twix.task_certification.certification.contract.TaskCertificationUiState
import com.twix.task_certification.certification.model.camera.Camera
import com.twix.task_certification.certification.model.camera.CameraPreview
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun TaskCertificationRoute(
    toastManager: ToastManager = koinInject(),
    camera: Camera = koinInject(),
    viewModel: TaskCertificationViewModel = koinViewModel(),
    navigateToBack: () -> Unit,
    navigateToDetail: (Long, LocalDate, BetweenUs) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val cameraPreview by camera.surfaceRequests.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val lifecycleOwner = LocalLifecycleOwner.current
    val coroutineScope = rememberCoroutineScope()

    val pickMedia =
        rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            viewModel.dispatch(TaskCertificationIntent.PickPicture(uri))
        }

    LaunchedEffect(uiState.lens) {
        camera.bind(lifecycleOwner, uiState.lens)
    }

    DisposableEffect(Unit) {
        onDispose {
            camera.unbind()
        }
    }

    LaunchedEffect(uiState.torch) {
        camera.toggleTorch(uiState.torch)
    }

    ObserveAsEvents(viewModel.sideEffect) { event ->
        when (event) {
            is TaskCertificationSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(event.message),
                        type = event.type,
                    ),
                )
            }

            TaskCertificationSideEffect.NavigateToBack -> navigateToBack()
            is TaskCertificationSideEffect.NavigateToDetail ->
                navigateToDetail(
                    event.goalId,
                    event.date,
                    event.betweenUs,
                )
        }
    }

    if (uiState.isLoading) {
        LoadingContent()
    } else {
        TaskCertificationScreen(
            uiState = uiState,
            cameraPreview = cameraPreview,
            onClickClose = navigateToBack,
            onCaptureClick = {
                coroutineScope.launch {
                    camera
                        .takePicture()
                        .onSuccess {
                            viewModel.dispatch(TaskCertificationIntent.TakePicture(it))
                        }.onFailure {
                            viewModel.dispatch(TaskCertificationIntent.TakePicture(null))
                        }
                }
            },
            onToggleCameraClick = {
                viewModel.dispatch(TaskCertificationIntent.ToggleLens)
            },
            onClickFlash = {
                viewModel.dispatch(TaskCertificationIntent.ToggleTorch)
            },
            onClickGallery = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClickRefresh = {
                viewModel.dispatch(TaskCertificationIntent.RetakePicture)
            },
            onCommentChanged = {
                viewModel.dispatch(TaskCertificationIntent.UpdateComment(it))
            },
            onFocusChanged = {
                viewModel.dispatch(TaskCertificationIntent.CommentFocusChanged(it))
            },
            onClickUpload = {
                viewModel.dispatch(TaskCertificationIntent.TryUpload)
            },
        )
    }
}

@Composable
private fun TaskCertificationScreen(
    uiState: TaskCertificationUiState,
    cameraPreview: CameraPreview?,
    onClickClose: () -> Unit,
    onCaptureClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    onClickFlash: () -> Unit,
    onClickGallery: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickUpload: () -> Unit,
    onCommentChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var previewBoxBottom by remember { mutableFloatStateOf(0f) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrayColor.C500)
                .noRippleClickable { focusManager.clearFocus() },
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TaskCertificationTopBar(onClickClose = onClickClose)

            Spacer(modifier = Modifier.height(24.26.dp))

            AnimatedContent(targetState = uiState.showCommentError) { isError ->
                if (isError) {
                    CommentErrorText()
                } else {
                    AppText(
                        text = stringResource(R.string.task_certification_take_picture),
                        style = AppTextStyle.H2,
                        color = GrayColor.C100,
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            CameraPreviewBox(
                showTorch = uiState.showTorch,
                capture = uiState.capture,
                previewRequest = cameraPreview,
                torch = uiState.torch,
                onClickFlash = onClickFlash,
                onPositioned = { previewBoxBottom = it },
            )

            Spacer(modifier = Modifier.height(52.dp))

            CameraControlBar(
                capture = uiState.capture,
                onCaptureClick = onCaptureClick,
                onToggleCameraClick = onToggleCameraClick,
                onClickGallery = onClickGallery,
                onClickRefresh = onClickRefresh,
                onClickUpload = onClickUpload,
            )
        }

        CommentAnchorFrame(
            uiModel = uiState.comment,
            anchorBottom = previewBoxBottom,
            onCommentChanged = onCommentChanged,
            onFocusChanged = onFocusChanged,
        )
    }
}

@Preview
@Composable
fun TaskCertificationScreenPreview() {
    TwixTheme {
        TaskCertificationScreen(
            uiState = TaskCertificationUiState(),
            cameraPreview = null,
            onClickClose = {},
            onCaptureClick = {},
            onToggleCameraClick = {},
            onClickFlash = {},
            onClickGallery = {},
            onClickRefresh = {},
            onClickUpload = {},
            onCommentChanged = {},
            onFocusChanged = {},
        )
    }
}
