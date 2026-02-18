package com.twix.task_certification.detail

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.components.photolog.BackgroundCard
import com.twix.designsystem.components.photolog.ForegroundCard
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalReactionType
import com.twix.task_certification.R
import com.twix.task_certification.detail.component.TaskCertificationDetailTopBar
import com.twix.task_certification.detail.model.TaskCertificationDetailIntent
import com.twix.task_certification.detail.model.TaskCertificationDetailSideEffect
import com.twix.task_certification.detail.model.TaskCertificationDetailUiState
import com.twix.task_certification.detail.preview.TaskCertificationDetailPreviewProvider
import com.twix.task_certification.detail.reaction.ReactionBar
import com.twix.task_certification.detail.reaction.ReactionEffect
import com.twix.task_certification.detail.reaction.ReactionUiModel
import com.twix.task_certification.detail.swipe.SwipeableCard
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.hasCameraPermission
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import com.twix.designsystem.R as DesR

@Composable
fun TaskCertificationDetailRoute(
    navigateToBack: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToEditor: (TaskCertificationDetailUiState) -> Unit,
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
                                DesR.string.toast_camera_permission_request,
                            ),
                            ToastType.ERROR,
                        ),
                    )
                }
            }
        }

    TaskCertificationDetailScreen(
        uiState = uiState,
        onBack = navigateToBack,
        onClickModify = { navigateToEditor(uiState) },
        onClickReaction = { viewModel.dispatch(TaskCertificationDetailIntent.Reaction(it)) },
        onClickUpload = {
            if (currentContext.hasCameraPermission()) {
                navigateToCertification(uiState.goalId, uiState.selectedDate)
            } else {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        },
        onClickSting = { viewModel.dispatch(TaskCertificationDetailIntent.Sting) },
        onSwipe = { viewModel.dispatch(TaskCertificationDetailIntent.SwipeCard) },
    )
}

@Composable
fun TaskCertificationDetailScreen(
    uiState: TaskCertificationDetailUiState,
    onBack: () -> Unit,
    onClickModify: () -> Unit,
    onClickReaction: (GoalReactionType) -> Unit,
    onClickUpload: () -> Unit,
    onClickSting: () -> Unit,
    onSwipe: () -> Unit,
) {
    Scaffold(
        topBar = {
            TaskCertificationDetailTopBar(
                goalTitle = uiState.goalName,
                onBack = onBack,
                actionTitle = if (uiState.canModify) stringResource(DesR.string.word_modify) else null,
                onClickModify = if (uiState.canModify) onClickModify else null,
                modifier =
                    Modifier
                        .background(color = CommonColor.White),
            )
        },
    ) { innerPadding ->
        Column(
            Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(color = CommonColor.White),
        ) {
            Spacer(Modifier.height(103.dp))

            Box(Modifier.fillMaxWidth()) {
                BackgroundCard(
                    isCertificated = uiState.isDisplayedGoalCertificated,
                    uploadedAt = uiState.displayedGoalUpdateAt,
                    buttonTitle =
                        when (uiState.currentShow) {
                            BetweenUs.ME -> stringResource(R.string.task_certification_take_picture)
                            BetweenUs.PARTNER -> stringResource(R.string.task_certification_detail_partner_sting)
                        },
                    rotation =
                        when (uiState.currentShow) {
                            BetweenUs.ME -> -8f
                            BetweenUs.PARTNER -> 0f
                        },
                    onClick =
                        when (uiState.currentShow) {
                            BetweenUs.ME -> onClickUpload
                            BetweenUs.PARTNER -> onClickSting
                        },
                )

                SwipeableCard(
                    onSwipe = onSwipe,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    ForegroundCard(
                        isCertificated = uiState.isDisplayedGoalCertificated,
                        nickName = uiState.displayedNickname,
                        imageUrl = uiState.displayedGoalImageUrl,
                        comment = uiState.displayedGoalComment,
                        currentShow = uiState.currentShow,
                        rotation =
                            when (uiState.currentShow) {
                                BetweenUs.ME -> 0f
                                BetweenUs.PARTNER -> -8f
                            },
                    )
                }
            }

            ReactionSection(
                visible = uiState.canReaction,
                reaction = uiState.partnerPhotolog?.reaction,
                onClickReaction = onClickReaction,
            )
        }
    }
}

@Composable
private fun ReactionSection(
    visible: Boolean,
    reaction: GoalReactionType? = null,
    onClickReaction: (GoalReactionType) -> Unit,
) {
    if (!visible) return
    var effectTarget by remember { mutableStateOf<ReactionUiModel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Bottom,
        ) {
            Spacer(Modifier.height(85.dp))
            ReactionBar(
                selectedReaction = reaction,
                onSelectReaction = { type ->
                    onClickReaction(type)
                    effectTarget = ReactionUiModel.find(type)
                },
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp),
            )
        }

        ReactionEffect(
            targetReaction = effectTarget,
            modifier = Modifier.padding(bottom = 100.dp),
        )
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
            onClickSting = {},
            onSwipe = {},
        )
    }
}
