package com.twix.home

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
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
        onPokeGoal = { viewModel.dispatch(HomeIntent.PokeGoal(it)) }
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

            Spacer(Modifier.height(12.dp))

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
                    onVerificationClick = onVerificationClick,
                    onEditClick = onEditClick,
                    onClickGoalCard = onClickCard,
                    onPokeGoal = onPokeGoal,
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
    onVerificationClick: (Long, GoalCheckState) -> Unit,
    onClickGoalCard: (Long, LocalDate, BetweenUs) -> Unit,
    onEditClick: () -> Unit,
    onPokeGoal: (Long) -> Unit,
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

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 20.dp),
    ) {
        item {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppText(
                    text = title,
                    style = AppTextStyle.B1,
                    color = GrayColor.C500,
                )

                Spacer(Modifier.weight(1f))

                Image(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .noRippleClickable { onEditClick() },
                )
            }
        }

        items(goals, key = { it.goalId }) { goal ->
            GoalCardFrame(
                modifier =
                    Modifier
                        .fillMaxWidth(),
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
                .background(GrayColor.C500, CircleShape)
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
