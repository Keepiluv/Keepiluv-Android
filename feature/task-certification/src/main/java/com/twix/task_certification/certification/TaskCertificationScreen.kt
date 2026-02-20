package com.twix.task_certification.certification

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.components.comment.CIRCLE_SIZE
import com.twix.designsystem.components.comment.CommentBox
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.DimmedColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.task_certification.R
import com.twix.task_certification.certification.camera.Camera
import com.twix.task_certification.certification.component.CameraControlBar
import com.twix.task_certification.certification.component.CameraPreviewBox
import com.twix.task_certification.certification.component.CommentErrorText
import com.twix.task_certification.certification.component.TaskCertificationTopBar
import com.twix.task_certification.certification.model.CameraPreview
import com.twix.task_certification.certification.model.TaskCertificationIntent
import com.twix.task_certification.certification.model.TaskCertificationSideEffect
import com.twix.task_certification.certification.model.TaskCertificationUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun TaskCertificationRoute(
    toastManager: ToastManager = koinInject(),
    camera: Camera = koinInject(),
    viewModel: TaskCertificationViewModel = koinViewModel(),
    navigateToBack: () -> Unit,
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

    DisposableEffect(lifecycleOwner, uiState.lens) {
        coroutineScope.launch {
            camera.bind(lifecycleOwner, uiState.lens)
        }

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

            TaskCertificationSideEffect.NavigateToDetail -> navigateToBack()
        }
    }

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
    val density = LocalDensity.current
    val imeBottom = WindowInsets.ime.getBottom(density)

    /**
     * Comment Circle UI의 높이
     * */
    val commentBoxHeight = with(density) { CIRCLE_SIZE.toPx() }

    /**
     * [기본 위치 설정]
     * 프리뷰 박스 하단(previewBoxBottom)을 기준으로 배치
     * circlePx * 2 만큼 위로 올리고 패딩(+20f)
     */
    val defaultY = previewBoxBottom - (commentBoxHeight * 2) + 20f

    /**
     * 키보드가 올라왔을 때 CommentBox와 키보드 사이의 최소 간격
     */
    val keyboardPadding = 60f

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrayColor.C500)
                .noRippleClickable { focusManager.clearFocus() },
    ) {
        Column(
            Modifier
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
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

        /**
         * 프리뷰 박스의 하단 좌표(previewBoxBottom)가 측정되었을 때만 렌더링 시작
         * */
        if (previewBoxBottom != 0f) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                /**
                 * 화면의 전체 높이와 키보드 상단 경계선 좌표 계산
                 * */
                val screenHeight = constraints.maxHeight.toFloat()
                val keyboardTop = screenHeight - imeBottom

                /**
                 * Dimmed 배경 레이어
                 * 키보드가 올라왔을 때만 나타나도록 하며, 클릭 시 포커스를 해제
                 */
                AnimatedVisibility(
                    visible = uiState.commentUiModel.isFocused,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(DimmedColor.D070)
                                .noRippleClickable { focusManager.clearFocus() },
                    )
                }

                CommentBox(
                    uiModel = uiState.commentUiModel,
                    onCommentChanged = onCommentChanged,
                    onFocusChanged = onFocusChanged,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .offset {
                                IntOffset(
                                    x = 0,
                                    y =
                                        if (imeBottom > 0 && (defaultY + commentBoxHeight) > keyboardTop) {
                                            /**
                                             * 키보드가 활성화되었고(imeBottom > 0),
                                             * 기존 위치(defaultY)가 키보드에 가려질 상황일 때만 실행됩니다.
                                             * imeBottom은 키보드가 닫히기 시작하면 실시간으로 줄어들어 0에 도달합니다.
                                             * 이때 if (imeBottom > 0) 조건이 false로 바뀌면서 즉시 else 문인 defaultY 위치로 점프하게 되는데,
                                             * 만약 시스템의 WindowInsets 애니메이션이 끝나기 전이나 레이아웃 재계산 중에 일시적으로 기준 좌표를 잃어
                                             * CommentBox가 화면 최하단을 갔다 원래 위치로 돌아오는 "튀는" 현상이 발생합니다.
                                             */
                                            (screenHeight - imeBottom - commentBoxHeight - keyboardPadding).toInt()
                                        } else {
                                            defaultY.toInt()
                                        },
                                )
                            },
                )
            }
        }
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
