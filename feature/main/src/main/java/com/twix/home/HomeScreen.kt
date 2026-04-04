package com.twix.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.calendar.WeeklyCalendar
import com.twix.designsystem.components.goal.EmptyGoalGuide
import com.twix.designsystem.components.goal.GoalCardFrame
import com.twix.designsystem.components.goal.GoalCheckIndicator
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.extension.showCameraPermissionToastWithNavigateToSettingAction
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalCheckState
import com.twix.domain.model.goal.Goal
import com.twix.domain.model.goal.checkState
import com.twix.home.component.GoalVerifications
import com.twix.home.component.HomeTopBar
import com.twix.home.model.HomeUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.findActivity
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate
import kotlin.math.roundToInt

@Composable
fun HomeRoute(
    viewModel: HomeViewModel = koinViewModel(),
    toastManager: ToastManager = koinInject(),
    onShowCalendarBottomSheet: () -> Unit,
    navigateToGoalEditor: () -> Unit,
    navigateToGoalManage: (LocalDate) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToCertificationDetail: (Long, LocalDate, BetweenUs) -> Unit,
    navigateToNotification: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val coroutineScope = rememberCoroutineScope()

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission(),
        ) { granted ->
            if (granted) {
                navigateToCertification(uiState.selectedGoalId, uiState.selectedDate)
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

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            HomeSideEffect.ShowPermissionLauncher -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }

            is HomeSideEffect.ShowToast ->
                toastManager.tryShow(
                    ToastData(
                        currentContext.getString(sideEffect.resId),
                        sideEffect.type,
                    ),
                )

            HomeSideEffect.ShowMonthPickerBottomSheet -> Unit
            is HomeSideEffect.ShowPokeToast ->
                toastManager.show(
                    ToastData(
                        sideEffect.message,
                        ToastType.SUCCESS,
                    ),
                )
        }
    }

    HomeScreen(
        uiState = uiState,
        onSelectDate = { viewModel.dispatch(HomeIntent.SelectDate(it)) },
        onPreviousWeek = { viewModel.dispatch(HomeIntent.PreviousWeek) },
        onNextWeek = { viewModel.dispatch(HomeIntent.NextWeek) },
        onUpdateVisibleDate = { viewModel.dispatch(HomeIntent.UpdateVisibleDate(it)) },
        onMoveToToday = { viewModel.dispatch(HomeIntent.MoveToToday) },
        onShowCalendarBottomSheet = onShowCalendarBottomSheet,
        onAddNewGoal = navigateToGoalEditor,
        onEditClick = { navigateToGoalManage(uiState.selectedDate) },
        onVerificationClick = { goalId, goalCheckState ->
            viewModel.dispatch(HomeIntent.Verification(goalId, goalCheckState))
        },
        onClickCard = navigateToCertificationDetail,
        onSettingClick = navigateToSettings,
        onNotificationClick = navigateToNotification,
        onPokeGoal = { viewModel.dispatch(HomeIntent.PokeGoal(it)) },
        onRefresh = { viewModel.dispatch(HomeIntent.Refresh) },
    )
}

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onUpdateVisibleDate: (LocalDate) -> Unit,
    onMoveToToday: () -> Unit,
    onShowCalendarBottomSheet: () -> Unit,
    onAddNewGoal: () -> Unit,
    onEditClick: () -> Unit,
    onVerificationClick: (Long, GoalCheckState) -> Unit,
    onClickCard: (Long, LocalDate, BetweenUs) -> Unit,
    onSettingClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onPokeGoal: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            HomeTopBar(
                monthYearText = uiState.monthYear,
                onNotificationClick = onNotificationClick,
                onSettingClick = onSettingClick,
                onMoveToToday = onMoveToToday,
                onShowCalendarBottomSheet = onShowCalendarBottomSheet,
            )

            WeeklyCalendar(
                selectedDate = uiState.selectedDate,
                referenceDate = uiState.referenceDate,
                onSelectDate = onSelectDate,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
                onUpdateVisibleDate = onUpdateVisibleDate,
            )

            if (uiState.goalList.goals.isEmpty()) {
                EmptyGoalGuide(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.home_empty_goal_guide),
                )
            } else {
                GoalList(
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .weight(1f),
                    goals = uiState.goalList.goals,
                    selectedDate = uiState.selectedDate,
                    isRefreshing = uiState.isRefreshing,
                    onVerificationClick = onVerificationClick,
                    onEditClick = onEditClick,
                    onClickGoalCard = onClickCard,
                    onPokeGoal = onPokeGoal,
                    onRefresh = onRefresh,
                )
            }
        }

        AddGoalButton(
            modifier =
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 12.dp, end = 16.dp),
            onClick = onAddNewGoal,
        )
    }
}

@Composable
fun GoalList(
    modifier: Modifier = Modifier,
    goals: List<Goal>,
    selectedDate: LocalDate,
    isRefreshing: Boolean,
    onVerificationClick: (Long, GoalCheckState) -> Unit,
    onClickGoalCard: (Long, LocalDate, BetweenUs) -> Unit,
    onEditClick: () -> Unit,
    onPokeGoal: (Long) -> Unit,
    onRefresh: () -> Unit,
) {
    val today = remember { LocalDate.now() }
    val titleRes =
        remember(selectedDate, today) {
            when {
                selectedDate < today -> R.string.goal_list_title_previous
                selectedDate > today -> R.string.goal_list_title_next
                else -> R.string.goal_list_title_today
            }
        }
    val title = stringResource(titleRes)

    val listState = rememberLazyListState()
    val density = androidx.compose.ui.platform.LocalDensity.current

    val refreshTriggerPx = with(density) { 88.dp.toPx() }
    val refreshingHoldPx = with(density) { 56.dp.toPx() }
    val maxPullPx = with(density) { 140.dp.toPx() }

    var pullOffsetPx by remember { mutableFloatStateOf(0f) }

    val animatedPullOffsetPx by animateFloatAsState(
        targetValue =
            when {
                isRefreshing -> refreshingHoldPx
                else -> pullOffsetPx
            },
        animationSpec = spring(),
        label = "pull_offset",
    )

    LaunchedEffect(isRefreshing) {
        if (!isRefreshing) {
            pullOffsetPx = 0f
        }
    }

    val nestedScrollConnection =
        remember(listState, isRefreshing) {
            object : NestedScrollConnection {
                override fun onPreScroll(
                    available: Offset,
                    source: NestedScrollSource,
                ): Offset {
                    if (source != NestedScrollSource.Drag) return Offset.Zero

                    val isAtTop =
                        listState.firstVisibleItemIndex == 0 &&
                            listState.firstVisibleItemScrollOffset == 0

                    val delta = available.y

                    if (delta > 0 && isAtTop && !isRefreshing) {
                        val newOffset =
                            (pullOffsetPx + (delta * 0.5f))
                                .coerceAtMost(maxPullPx)
                        val consumed = newOffset - pullOffsetPx
                        pullOffsetPx = newOffset
                        return Offset(x = 0f, y = consumed / 0.5f)
                    }

                    if (delta < 0 && pullOffsetPx > 0f) {
                        val newOffset = (pullOffsetPx + delta).coerceAtLeast(0f)
                        val consumed = newOffset - pullOffsetPx
                        pullOffsetPx = newOffset
                        return Offset(x = 0f, y = consumed)
                    }

                    return Offset.Zero
                }

                override suspend fun onPreFling(available: Velocity): Velocity {
                    if (pullOffsetPx >= refreshTriggerPx && !isRefreshing) {
                        onRefresh()
                    } else if (!isRefreshing) {
                        pullOffsetPx = 0f
                    }
                    return Velocity.Zero
                }
            }
        }

    Box(
        modifier = modifier.nestedScroll(nestedScrollConnection),
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding =
                PaddingValues(
                    top = with(density) { animatedPullOffsetPx.toDp() },
                    bottom = 20.dp,
                ),
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(
                        text = title,
                        style = AppTextStyle.B1,
                        color = GrayColor.C500,
                    )

                    Spacer(Modifier.weight(1f))

                    AppText(
                        text = stringResource(R.string.word_edit),
                        style = AppTextStyle.B1,
                        color = GrayColor.C500,
                        modifier =
                            Modifier
                                .noRippleClickable(onClick = onEditClick)
                    )
                }
            }

            items(goals, key = { it.goalId }) { goal ->
                GoalCardFrame(
                    modifier = Modifier.fillMaxWidth(),
                    goalName = goal.name,
                    goalIcon = goal.icon,
                    right = {
                        GoalCheckIndicator(
                            state = goal.checkState(),
                            onClick = { onVerificationClick(goal.goalId, it) },
                        )
                    },
                    content = {
                        if (goal.myVerification != null || goal.partnerVerification != null) {
                            GoalVerifications(
                                myVerification = goal.myVerification,
                                partnerVerification = goal.partnerVerification,
                                onMyClick = {
                                    onClickGoalCard(
                                        goal.goalId,
                                        selectedDate,
                                        BetweenUs.ME,
                                    )
                                },
                                onPartnerClick = {
                                    onClickGoalCard(
                                        goal.goalId,
                                        selectedDate,
                                        BetweenUs.PARTNER,
                                    )
                                },
                                onPokeGoal = { onPokeGoal(goal.goalId) },
                            )
                        }
                    },
                )
            }
        }

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

@Composable
private fun AddGoalButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            modifier
                .size(56.dp)
                .shadow(
                    elevation = 8.dp,
                    shape = CircleShape,
                    clip = false,
                ).background(GrayColor.C500, CircleShape)
                .border(1.dp, GrayColor.C300, CircleShape)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = "add goal",
            modifier =
                Modifier
                    .size(40.dp),
            colorFilter = ColorFilter.tint(CommonColor.White),
        )
    }
}
