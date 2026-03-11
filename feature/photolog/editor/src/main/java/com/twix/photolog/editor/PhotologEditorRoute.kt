package com.twix.photolog.editor

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.twix.designsystem.R
import com.twix.designsystem.components.comment.CommentAnchorFrame
import com.twix.designsystem.components.photolog.PhotologCard
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.photolog.editor.component.PhotologEditorTopBar
import com.twix.photolog.editor.component.RetakeButton
import com.twix.photolog.editor.contract.PhotologEditorIntent
import com.twix.photolog.editor.contract.PhotologEditorSideEffect
import com.twix.photolog.editor.contract.PhotologEditorUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.hasCameraPermission
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun PhotologEditorRoute(
    navigateToBack: () -> Unit,
    navigateToCertification: (Long, Long, String, LocalDate) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: PhotologEditorViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val coroutineScope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is PhotologEditorSideEffect.ShowToast ->
                toastManager.tryShow(
                    ToastData(
                        currentContext.getString(sideEffect.message),
                        sideEffect.type,
                    ),
                )
            is PhotologEditorSideEffect.ShowPokeToast -> {
                toastManager.tryShow(
                    ToastData(sideEffect.message, ToastType.SUCCESS),
                )
            }
        }
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->

            if (granted) {
                navigateToCertification(
                    uiState.goalId,
                    uiState.photologId,
                    uiState.comment.value,
                    uiState.selectedDate,
                )
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

    PhotologEditorScreen(
        uiState = uiState,
        onBack = navigateToBack,
        onClickSave = { viewModel.dispatch(PhotologEditorIntent.Save) },
        onFocusChanged = { viewModel.dispatch(PhotologEditorIntent.CommentFocusChanged(it)) },
        onCommentChanged = { viewModel.dispatch(PhotologEditorIntent.ModifyComment(it)) },
        onClickRetake = {
            if (currentContext.hasCameraPermission()) {
                navigateToCertification(
                    uiState.goalId,
                    uiState.photologId,
                    uiState.comment.value,
                    uiState.selectedDate,
                )
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
    )
}

@Composable
fun PhotologEditorScreen(
    uiState: PhotologEditorUiState,
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
            PhotologEditorTopBar(
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

            RetakeButton(onClickRetake = onClickRetake)
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
private fun PhotologEditorScreenPreview() {
    TwixTheme {
        PhotologEditorScreen(
            uiState =
                PhotologEditorUiState(
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
