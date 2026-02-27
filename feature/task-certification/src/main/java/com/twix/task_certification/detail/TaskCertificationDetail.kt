package com.twix.task_certification.detail

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.GoalReactionType
import com.twix.task_certification.detail.component.TaskCertificationCardContent
import com.twix.task_certification.detail.component.TaskCertificationDetailTopBar
import com.twix.task_certification.detail.component.reaction.ReactionContent
import com.twix.task_certification.detail.component.reaction.ReactionEffect
import com.twix.task_certification.detail.component.reaction.ReactionEffectSpec
import com.twix.task_certification.detail.contract.TaskCertificationDetailIntent
import com.twix.task_certification.detail.contract.TaskCertificationDetailSideEffect
import com.twix.task_certification.detail.contract.TaskCertificationDetailUiState
import com.twix.task_certification.detail.preview.TaskCertificationDetailPreviewProvider
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.hasCameraPermission
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun TaskCertificationDetailRoute(
    navigateToBack: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToEditor: (Long, LocalDate) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: TaskCertificationDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val coroutineScope = rememberCoroutineScope()

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is TaskCertificationDetailSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(currentContext.getString(sideEffect.message), sideEffect.type),
                )
            }

            is TaskCertificationDetailSideEffect.ShowPokeToast -> {
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
                navigateToCertification(uiState.goalId, uiState.selectedDate)
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

    BoxWithConstraints {
        val density = LocalDensity.current
        val screenHeightPx = with(density) { maxHeight.toPx() }

        TaskCertificationDetailScreen(
            uiState = uiState,
            onBack = navigateToBack,
            onClickModify = {
                navigateToEditor(
                    uiState.goalId,
                    uiState.selectedDate,
                )
            },
            onClickReaction = { viewModel.dispatch(TaskCertificationDetailIntent.Reaction(it)) },
            onClickUpload = {
                if (currentContext.hasCameraPermission()) {
                    navigateToCertification(uiState.goalId, uiState.selectedDate)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onPoke = { viewModel.dispatch(TaskCertificationDetailIntent.Poke) },
            onSwipe = { viewModel.dispatch(TaskCertificationDetailIntent.SwipeCard) },
        )
        if (!uiState.hasShownMyReaction && uiState.isDisplayedMyPhotolog) {
            val model = uiState.myReaction
            if (model != null) {
                ReactionEffect(
                    targetReaction = model,
                    spec =
                        ReactionEffectSpec(
                            particleCount = 10,
                            durationRange = 500..800,
                            // 전체 화면 높이까지 퍼짐
                            travelDistanceRange = 500..screenHeightPx.toInt(),
                        ),
                    onFinished = {
                        viewModel.dispatch(TaskCertificationDetailIntent.MyReactionEffected)
                    },
                )
            }
        }
    }
}

@Composable
fun TaskCertificationDetailScreen(
    uiState: TaskCertificationDetailUiState,
    onBack: () -> Unit,
    onClickModify: () -> Unit,
    onClickReaction: (GoalReactionType) -> Unit,
    onClickUpload: () -> Unit,
    onPoke: () -> Unit,
    onSwipe: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = CommonColor.White),
    ) {
        TaskCertificationDetailTopBar(
            title = uiState.goalName,
            canModify = uiState.canModify,
            onBack = onBack,
            onClickModify = onClickModify,
        )
        Spacer(Modifier.height(103.dp))

        if (uiState.isLoading) {
            TaskCertificationCardContent(
                uiState = uiState,
                onSwipe = onSwipe,
                onClickUpload = onClickUpload,
                onPoke = onPoke,
            )

            if (uiState.canReaction) {
                ReactionContent(
                    reaction = uiState.partnerPhotolog?.reaction,
                    onClickReaction = onClickReaction,
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCertificationDetailScreenPreview(
    @PreviewParameter(TaskCertificationDetailPreviewProvider::class)
    uiState: TaskCertificationDetailUiState,
) {
    TwixTheme {
        TaskCertificationDetailScreen(
            uiState = uiState,
            onBack = {},
            onClickModify = {},
            onClickReaction = {},
            onClickUpload = {},
            onPoke = {},
            onSwipe = {},
        )
    }
}
