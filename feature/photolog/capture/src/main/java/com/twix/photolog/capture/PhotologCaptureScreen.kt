package com.twix.photolog.capture

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
import com.twix.photolog.capture.component.CameraControlBar
import com.twix.photolog.capture.component.CameraPreviewBox
import com.twix.photolog.capture.component.CommentErrorText
import com.twix.photolog.capture.component.LoadingContent
import com.twix.photolog.capture.component.PhotologCaptureTopBar
import com.twix.photolog.capture.contract.PhotologCaptureIntent
import com.twix.photolog.capture.contract.PhotologCaptureSideEffect
import com.twix.photolog.capture.contract.PhotologCaptureUiState
import com.twix.photolog.capture.model.camera.Camera
import com.twix.photolog.capture.model.camera.CameraPreview
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun PhotologCaptureRoute(
    toastManager: ToastManager = koinInject(),
    camera: Camera = koinInject(),
    viewModel: PhotologCaptureViewModel = koinViewModel(),
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
            viewModel.dispatch(PhotologCaptureIntent.PickPicture(uri))
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
            is PhotologCaptureSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(event.message),
                        type = event.type,
                    ),
                )
            }

            PhotologCaptureSideEffect.NavigateToBack -> navigateToBack()
            is PhotologCaptureSideEffect.NavigateToDetail ->
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
        PhotologCaptureScreen(
            uiState = uiState,
            cameraPreview = cameraPreview,
            onClickClose = navigateToBack,
            onCaptureClick = {
                coroutineScope.launch {
                    camera
                        .takePicture()
                        .onSuccess {
                            viewModel.dispatch(PhotologCaptureIntent.TakePicture(it))
                        }.onFailure {
                            viewModel.dispatch(PhotologCaptureIntent.TakePicture(null))
                        }
                }
            },
            onToggleCameraClick = {
                viewModel.dispatch(PhotologCaptureIntent.ToggleLens)
            },
            onClickTorch = {
                viewModel.dispatch(PhotologCaptureIntent.ToggleTorch)
            },
            onClickGallery = {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            },
            onClickRefresh = {
                viewModel.dispatch(PhotologCaptureIntent.RetakePicture)
            },
            onCommentChanged = {
                viewModel.dispatch(PhotologCaptureIntent.UpdateComment(it))
            },
            onFocusChanged = {
                viewModel.dispatch(PhotologCaptureIntent.CommentFocusChanged(it))
            },
            onClickUpload = {
                viewModel.dispatch(PhotologCaptureIntent.TryUpload)
            },
        )
    }
}

@Composable
private fun PhotologCaptureScreen(
    uiState: PhotologCaptureUiState,
    cameraPreview: CameraPreview?,
    onClickClose: () -> Unit,
    onCaptureClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    onClickTorch: () -> Unit,
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
            PhotologCaptureTopBar(onClickClose = onClickClose)

            Spacer(modifier = Modifier.height(24.26.dp))

            AnimatedContent(targetState = uiState.showCommentError) { isError ->
                if (isError) {
                    CommentErrorText()
                } else {
                    AppText(
                        text = stringResource(R.string.photolog_take_picture),
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
                onClickTorch = onClickTorch,
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
fun PhotologCaptureScreenPreview() {
    TwixTheme {
        PhotologCaptureScreen(
            uiState = PhotologCaptureUiState(),
            cameraPreview = null,
            onClickClose = {},
            onCaptureClick = {},
            onToggleCameraClick = {},
            onClickTorch = {},
            onClickGallery = {},
            onClickRefresh = {},
            onClickUpload = {},
            onCommentChanged = {},
            onFocusChanged = {},
        )
    }
}
