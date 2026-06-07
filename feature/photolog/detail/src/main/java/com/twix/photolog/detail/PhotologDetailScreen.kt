package com.twix.photolog.detail

import android.Manifest
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.error.ErrorScreen
import com.twix.designsystem.components.loading.TwixLoadingOverlay
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.time.CooldownTime
import com.twix.photolog.detail.component.PhotologCardContent
import com.twix.photolog.detail.component.PhotologDetailTopBar
import com.twix.photolog.detail.component.reaction.ReactionContent
import com.twix.photolog.detail.component.reaction.ReactionEffect
import com.twix.photolog.detail.component.reaction.ReactionEffectSpec
import com.twix.photolog.detail.contract.PhotologDetailIntent
import com.twix.photolog.detail.contract.PhotologDetailSideEffect
import com.twix.photolog.detail.contract.PhotologDetailUiState
import com.twix.photolog.detail.preview.PhotologDetailPreviewProvider
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.hasCameraPermission
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun PhotologDetailRoute(
    navigateToBack: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToEditor: (Long, LocalDate) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: PhotologDetailViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is PhotologDetailSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(currentContext.getString(sideEffect.message), sideEffect.type),
                )
            }

            PhotologDetailSideEffect.ShowPokeToast -> {
                toastManager.tryShow(
                    ToastData(
                        currentContext.getString(R.string.toast_poke_goal_success),
                        ToastType.LIKE,
                    ),
                )
            }

            is PhotologDetailSideEffect.ShowPokeCooldownToast -> {
                val cooldownTime = CooldownTime.from(sideEffect.remainingMs)
                val timeLabel = formatCooldownTime(context, cooldownTime)
                toastManager.tryShow(
                    ToastData(
                        currentContext.getString(R.string.toast_poke_cooldown, timeLabel),
                        ToastType.ERROR,
                    ),
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
        val screenHeightPx = with(density) { maxHeight.toPx() }

        PhotologDetailScreen(
            uiState = uiState,
            screenHeightPx = screenHeightPx,
            onBack = navigateToBack,
            onRetry = { viewModel.dispatch(PhotologDetailIntent.Retry) },
            onRefresh = { viewModel.dispatch(PhotologDetailIntent.Refresh) },
            onClickModify = {
                navigateToEditor(
                    uiState.goalId,
                    uiState.selectedDate,
                )
            },
            onClickReaction = { viewModel.dispatch(PhotologDetailIntent.Reaction(it)) },
            onClickUpload = {
                if (currentContext.hasCameraPermission()) {
                    navigateToCertification(uiState.goalId, uiState.selectedDate)
                } else {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }
            },
            onPoke = { viewModel.dispatch(PhotologDetailIntent.Poke) },
            onSwipe = { viewModel.dispatch(PhotologDetailIntent.SwipeCard) },
        )
        if (!uiState.hasShownMyReaction && uiState.isDisplayedMyPhotolog) {
            val reaction = uiState.myReaction
            if (reaction != null) {
                ReactionEffect(
                    targetReaction = reaction,
                    spec =
                        ReactionEffectSpec(
                            particleCount = 10,
                            durationRange = 500..800,
                            travelDistanceRange = 500..screenHeightPx.toInt(),
                        ),
                    onFinished = {
                        viewModel.dispatch(PhotologDetailIntent.MyReactionEffected)
                    },
                )
            }
        }
    }
}

@Composable
fun PhotologDetailScreen(
    uiState: PhotologDetailUiState,
    screenHeightPx: Float,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onRefresh: () -> Unit,
    onClickModify: () -> Unit,
    onClickReaction: (GoalReactionType) -> Unit,
    onClickUpload: () -> Unit,
    onPoke: () -> Unit,
    onSwipe: () -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .background(color = CommonColor.White),
    ) {
        when {
            uiState.showLoading -> {
                TwixLoadingOverlay()
            }

            uiState.showError -> {
                ErrorScreen(
                    onClickRetry = onRetry,
                    onClickBack = onBack,
                )
            }

            else -> {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(color = CommonColor.White),
                ) {
                    PhotologDetailTopBar(
                        title = uiState.goalName,
                        canModify = uiState.canModify,
                        onBack = onBack,
                        onClickModify = onClickModify,
                    )

                    PhotologDetailRefreshContent(
                        modifier = Modifier.weight(1f),
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = onRefresh,
                    ) {
                        BoxWithConstraints(Modifier.fillMaxSize()) {
                            val cardSize = maxWidth - 54.dp
                            val reactionTopPadding = 103.dp + cardSize + 85.dp

                            Column(Modifier.fillMaxSize()) {
                                Spacer(Modifier.height(103.dp))

                                PhotologCardContent(
                                    uiState = uiState,
                                    isPokeDisabled = uiState.isPokeDisabled,
                                    onSwipe = onSwipe,
                                    onClickUpload = onClickUpload,
                                    onPoke = onPoke,
                                )
                            }

                            if (uiState.canReaction) {
                                ReactionContent(
                                    modifier =
                                        Modifier
                                            .align(Alignment.TopCenter)
                                            .padding(top = reactionTopPadding),
                                    screenHeightPx = screenHeightPx,
                                    reaction = uiState.partnerPhotolog?.reaction,
                                    onClickReaction = onClickReaction,
                                )
                            }
                        }
                    }
                }

                if (uiState.showContentLoading) {
                    TwixLoadingOverlay()
                }
            }
        }
    }
}

/**
 * 인증샷 상세 화면의 TopBar 하단 영역에 당겨서 새로고침 인터랙션을 제공한다.
 *
 * 상세 화면은 LazyColumn처럼 스크롤 가능한 자식이 없어 nested scroll 체인으로 드래그 이벤트를
 * 안정적으로 받을 수 없다. 그래서 이 컨테이너가 세로 드래그를 직접 감지해 아래로 당긴 거리만큼
 * 콘텐츠와 인디케이터를 함께 이동시키고, 기준 거리 이상에서 손을 떼면 [onRefresh]를 호출한다.
 *
 * [isRefreshing]이 true인 동안에는 콘텐츠를 고정 거리만큼 유지해 새로고침 진행 상태를 보여주고,
 * 완료되면 누적된 당김 offset을 초기화한다.
 */
@Composable
private fun PhotologDetailRefreshContent(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    content: @Composable () -> Unit,
) {
    val density = LocalDensity.current

    // 새로고침 실행 기준
    val refreshTriggerPx = with(density) { 88.dp.toPx() }
    // 새로고침 중 유지 거리
    val refreshingHoldPx = with(density) { 56.dp.toPx() }
    // 과도한 당김을 제한하는 값
    val maxPullPx = with(density) { 140.dp.toPx() }

    var pullOffsetPx by remember { mutableFloatStateOf(0f) }

    // 사용자 드래그 중에는 누적 offset을 따라가고, 새로고침 중에는 hold 위치에 고정한다.
    val animatedPullOffsetPx by animateFloatAsState(
        targetValue =
            when {
                isRefreshing -> refreshingHoldPx
                else -> pullOffsetPx
            },
        animationSpec = spring(),
        label = "photolog_detail_pull_offset",
    )

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            // 새로고침이 끝나면 다음 당김을 위해 누적 offset을 초기화한다.
            pullOffsetPx = 0f
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .pointerInput(isRefreshing) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 0 && !isRefreshing) {
                                // 아래로 당길 때는 저항감을 주기 위해 실제 드래그 거리의 절반만 반영한다.
                                val newOffset =
                                    (pullOffsetPx + (dragAmount * 0.5f))
                                        .coerceAtMost(maxPullPx)
                                pullOffsetPx = newOffset
                                change.consume()
                            }

                            if (dragAmount < 0 && pullOffsetPx > 0f) {
                                // 손가락을 위로 되돌릴 때는 누적된 당김 offset만큼만 다시 줄인다.
                                pullOffsetPx = (pullOffsetPx + dragAmount).coerceAtLeast(0f)
                                change.consume()
                            }
                        },
                        onDragEnd = {
                            // 기준 거리 이상 당긴 뒤 손을 떼면 새로고침을 시작하고, 부족하면 원위치로 복귀한다.
                            if (pullOffsetPx >= refreshTriggerPx && !isRefreshing) {
                                onRefresh()
                            } else if (!isRefreshing) {
                                pullOffsetPx = 0f
                            }
                        },
                        onDragCancel = {
                            if (!isRefreshing) {
                                pullOffsetPx = 0f
                            }
                        },
                    )
                },
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .offset {
                    IntOffset(
                        x = 0,
                        y = animatedPullOffsetPx.roundToInt(),
                    )
                }.background(color = CommonColor.White),
        ) {
            content()
        }

        // 당김 진행률만큼 indicator track을 점진적으로 드러낸다.
        val indicatorAlpha =
            if (animatedPullOffsetPx <= 0f) {
                0f
            } else {
                (animatedPullOffsetPx / refreshTriggerPx).coerceIn(0f, 1f)
            }

        Box(
            modifier =
                Modifier
                    .align(Alignment.TopCenter)
                    .offset {
                        IntOffset(
                            x = 0,
                            y =
                                ((animatedPullOffsetPx - with(density) { 32.dp.toPx() }) / 2f)
                                    .coerceAtLeast(0f)
                                    .roundToInt(),
                        )
                    },
            contentAlignment = Alignment.Center,
        ) {
            if (animatedPullOffsetPx > 0f || isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.5.dp,
                    color = GrayColor.C500,
                    trackColor = GrayColor.C100.copy(alpha = indicatorAlpha),
                )
            }
        }
    }
}

private fun formatCooldownTime(
    context: Context,
    cooldownTime: CooldownTime,
): String =
    when (cooldownTime) {
        is CooldownTime.Hours ->
            context.getString(
                R.string.hours_only,
                cooldownTime.value,
            )

        is CooldownTime.HoursAndMinutes ->
            context.getString(
                R.string.hours_minutes,
                cooldownTime.hours,
                cooldownTime.minutes,
            )

        is CooldownTime.Minutes ->
            context.getString(
                R.string.minutes_only,
                cooldownTime.value,
            )
    }

@Preview(showBackground = true)
@Composable
private fun PhotologDetailScreenPreview(
    @PreviewParameter(PhotologDetailPreviewProvider::class)
    uiState: PhotologDetailUiState,
) {
    TwixTheme {
        var previewState by remember { mutableStateOf(uiState) }
        PhotologDetailScreen(
            uiState = previewState,
            screenHeightPx = 0f,
            onBack = {},
            onRetry = {},
            onRefresh = {},
            onClickModify = {},
            onClickReaction = {},
            onClickUpload = {},
            onPoke = {},
            onSwipe = {
                previewState =
                    previewState.copy(
                        currentShow =
                            if (previewState.currentShow == BetweenUs.ME) {
                                BetweenUs.PARTNER
                            } else {
                                BetweenUs.ME
                            },
                    )
            },
        )
    }
}
