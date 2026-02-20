package com.twix.task_certification.editor

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppRoundButton
import com.twix.designsystem.components.comment.CommentAnchorFrame
import com.twix.designsystem.components.photolog.PhotologCard
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.task_certification.editor.component.TaskCertificationEditorTopBar
import com.twix.task_certification.editor.contract.TaskCertificationEditorIntent
import com.twix.task_certification.editor.contract.TaskCertificationEditorSideEffect
import com.twix.task_certification.editor.contract.TaskCertificationEditorUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.hasCameraPermission
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun TaskCertificationEditorRoute(
    navigateToBack: () -> Unit,
    navigateToCertification: (Long, Long, String) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: TaskCertificationEditorViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is TaskCertificationEditorSideEffect.ShowToast ->
                toastManager.tryShow(
                    ToastData(
                        currentContext.getString(sideEffect.message),
                        sideEffect.type,
                    ),
                )
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->

            if (granted) {
                navigateToCertification(uiState.goalId, uiState.photologId, uiState.comment.value)
                return@rememberLauncherForActivityResult
            }
            val activity = currentContext.findActivity() ?: return@rememberLauncherForActivityResult
            val shouldShowRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.CAMERA,
                )
            coroutineScope.launch {
                if (!shouldShowRationale) {
                    toastManager.showCameraPermissionToastWithNavigateToSettingAction(currentContext)
                } else {
                    toastManager.show(
                        ToastData(
                            currentContext.getString(
                                R.string.toast_camera_permission_request,
                            ),
                            ToastType.ERROR,
                        ),
                    )
                }
            }
        }

    TaskCertificationEditorScreen(
        uiState = uiState,
        onBack = navigateToBack,
        onClickSave = { viewModel.dispatch(TaskCertificationEditorIntent.Save) },
        onFocusChanged = { viewModel.dispatch(TaskCertificationEditorIntent.CommentFocusChanged(it)) },
        onCommentChanged = { viewModel.dispatch(TaskCertificationEditorIntent.ModifyComment(it)) },
        onClickRetake = {
            if (currentContext.hasCameraPermission()) {
                navigateToCertification(uiState.goalId, uiState.photologId, uiState.comment.value)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
    )
}

@Composable
fun TaskCertificationEditorScreen(
    uiState: TaskCertificationEditorUiState,
    onBack: () -> Unit,
    onClickSave: () -> Unit,
    onCommentChanged: (String) -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    onClickRetake: () -> Unit,
) {
    val focusManager = LocalFocusManager.current
    var photologBottom by remember { mutableFloatStateOf(0f) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = CommonColor.White)
                .noRippleClickable { focusManager.clearFocus() },
    ) {
        Column {
            TaskCertificationEditorTopBar(
                title = uiState.goalName,
                onBack = onBack,
                onClickSave = onClickSave,
            )

            Spacer(Modifier.height(103.dp))

            PhotologCard(
                modifier =
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            val bottom = coordinates.boundsInParent().bottom
                            if (photologBottom != bottom) {
                                photologBottom = bottom
                            }
                        },
            ) {
                AsyncImage(
                    model =
                        ImageRequest
                            .Builder(LocalContext.current)
                            .data(uiState.imageUrl)
                            .crossfade(true)
                            .build(),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                )
            }

            Spacer(Modifier.height(101.dp))

            AppRoundButton(
                text = stringResource(R.string.task_certification_editor_retake),
                textColor = GrayColor.C500,
                backgroundColor = CommonColor.White,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(68.dp)
                        .padding(horizontal = 30.dp)
                        .noRippleClickable { onClickRetake() },
            )
        }

        CommentAnchorFrame(
            uiModel = uiState.comment,
            anchorBottom = photologBottom,
            onCommentChanged = onCommentChanged,
            onFocusChanged = onFocusChanged,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCertificationEditorScreenPreview() {
    TwixTheme {
        TaskCertificationEditorScreen(
            uiState =
                TaskCertificationEditorUiState(
                    nickname = "페토",
                    goalName = "아이스크림 먹기",
                ),
            onBack = {},
            onClickSave = {},
            onFocusChanged = {},
            onClickRetake = {},
            onCommentChanged = {},
        )
    }
}
