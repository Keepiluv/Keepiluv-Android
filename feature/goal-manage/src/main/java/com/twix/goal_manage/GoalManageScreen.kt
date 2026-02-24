package com.twix.goal_manage

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.calendar.WeeklyCalendar
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.goal.EmptyGoalGuide
import com.twix.designsystem.components.goal.GoalCardFrame
import com.twix.designsystem.components.popup.CommonPopup
import com.twix.designsystem.components.popup.CommonPopupDivider
import com.twix.designsystem.components.popup.CommonPopupItem
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.extension.label
import com.twix.designsystem.extension.toRes
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.goal.GoalSummary
import com.twix.goal_manage.model.GoalManageUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun GoalManageRoute(
    selectedDate: LocalDate,
    toastManager: ToastManager = koinInject(),
    viewModel: GoalManageViewModel = koinViewModel(),
    popBackStack: () -> Unit,
    navigateToGoalEditor: (Long) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    LaunchedEffect(selectedDate) {
        viewModel.dispatch(GoalManageIntent.SetSelectedDate(selectedDate))
    }

    ObserveAsEvents(viewModel.sideEffect) { effect ->
        when (effect) {
            is GoalManageSideEffect.ShowToast -> toastManager.tryShow(ToastData(currentContext.getString(effect.resId), effect.type))
            is GoalManageSideEffect.NavigateToGoalEditor -> navigateToGoalEditor(effect.goalId)
        }
    }

    GoalManageScreen(
        uiState = uiState,
        openedMenuGoalId = uiState.openedMenuGoalId,
        pendingIds = uiState.pendingGoalIds,
        onBack = popBackStack,
        onSelectDate = { viewModel.dispatch(GoalManageIntent.SetSelectedDate(it)) },
        onPreviousWeek = { viewModel.dispatch(GoalManageIntent.PreviousWeek) },
        onNextWeek = { viewModel.dispatch(GoalManageIntent.NextWeek) },
        // 팝업 메뉴
        onOpenMenu = { viewModel.dispatch(GoalManageIntent.OpenMenu(it)) },
        onCloseMenu = { viewModel.dispatch(GoalManageIntent.CloseMenu) },
        // 팝업 아이템에서 요청
        onRequestEnd = { viewModel.dispatch(GoalManageIntent.ShowEndDialog(it)) },
        onRequestDelete = { viewModel.dispatch(GoalManageIntent.ShowDeleteDialog(it)) },
        // 다이얼로그 confirm
        onConfirmEnd = { viewModel.dispatch(GoalManageIntent.EndGoal(it)) },
        onConfirmDelete = { viewModel.dispatch(GoalManageIntent.DeleteGoal(it)) },
        // 다이얼로그 dismiss
        onDismissEndDialog = { viewModel.dispatch(GoalManageIntent.DismissEndDialog) },
        onDismissDeleteDialog = { viewModel.dispatch(GoalManageIntent.DismissDeleteDialog) },
        // 수정
        onEdit = { viewModel.dispatch(GoalManageIntent.EditGoal(it)) },
    )
}

@Composable
private fun GoalManageScreen(
    uiState: GoalManageUiState,
    openedMenuGoalId: Long?,
    onBack: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousWeek: () -> Unit,
    onNextWeek: () -> Unit,
    onEdit: (Long) -> Unit,
    onRequestDelete: (Long) -> Unit,
    onRequestEnd: (Long) -> Unit,
    onConfirmDelete: (Long) -> Unit,
    onConfirmEnd: (Long) -> Unit,
    onDismissDeleteDialog: () -> Unit,
    onDismissEndDialog: () -> Unit,
    onOpenMenu: (Long) -> Unit,
    onCloseMenu: () -> Unit,
    pendingIds: Set<Long>,
) {
    val endDialog = uiState.endDialog
    val deleteDialog = uiState.deleteDialog

    var endDialogSnapshot by remember { mutableStateOf(endDialog) }
    var deleteDialogSnapshot by remember { mutableStateOf(deleteDialog) }

    LaunchedEffect(endDialog) {
        if (endDialog != null) endDialogSnapshot = endDialog
    }
    LaunchedEffect(deleteDialog) {
        if (deleteDialog != null) deleteDialogSnapshot = deleteDialog
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(CommonColor.White),
        ) {
            CommonTopBar(
                title = stringResource(R.string.word_edit),
                left = {
                    Image(
                        painter = painterResource(R.drawable.ic_arrow3_left),
                        contentDescription = "back",
                        modifier =
                            Modifier
                                .padding(18.dp)
                                .size(24.dp)
                                .noRippleClickable(onClick = onBack),
                    )
                },
            )

            Spacer(Modifier.height(4.dp))

            WeeklyCalendar(
                selectedDate = uiState.selectedDate,
                referenceDate = uiState.referenceDate,
                onSelectDate = onSelectDate,
                onPreviousWeek = onPreviousWeek,
                onNextWeek = onNextWeek,
            )

            if (uiState.goalSummaries.isEmpty()) {
                EmptyGoalGuide(
                    modifier =
                        Modifier
                            .padding(top = 128.dp),
                    text = stringResource(R.string.goal_detail_empty_goal_guide),
                    isDetail = true,
                )
            } else {
                GoalSummaryList(
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .weight(1f),
                    summaryList = uiState.goalSummaries,
                    openedMenuGoalId = openedMenuGoalId,
                    pendingIds = pendingIds,
                    onOpenMenu = onOpenMenu,
                    onCloseMenu = onCloseMenu,
                    onEdit = onEdit,
                    onRequestDelete = onRequestDelete,
                    onRequestEnd = onRequestEnd,
                )
            }
        }

        CommonDialog(
            visible = endDialog != null,
            confirmText = stringResource(R.string.action_complete_goal),
            dismissText = stringResource(R.string.word_cancel),
            onDismissRequest = onDismissEndDialog,
            onConfirm = {
                val id = endDialog?.goalId
                onDismissEndDialog()
                id?.let(onConfirmEnd)
            },
            onDismiss = onDismissEndDialog,
            content = {
                val dialog = endDialogSnapshot ?: return@CommonDialog
                GoalSummaryDialogContent(
                    title = stringResource(R.string.dialog_end_goal_title, dialog.name),
                    content = stringResource(R.string.dialog_end_goal_content),
                    icon = dialog.icon,
                )
            },
        )

        CommonDialog(
            visible = deleteDialog != null,
            confirmText = stringResource(R.string.word_delete),
            dismissText = stringResource(R.string.word_cancel),
            onDismissRequest = onDismissDeleteDialog,
            onConfirm = {
                val id = deleteDialog?.goalId
                onDismissDeleteDialog()
                id?.let(onConfirmDelete)
            },
            onDismiss = onDismissDeleteDialog,
            content = {
                val dialog = deleteDialogSnapshot ?: return@CommonDialog
                GoalSummaryDialogContent(
                    title = stringResource(R.string.dialog_delete_goal_title, dialog.name),
                    content = stringResource(R.string.dialog_delete_goal_content),
                    icon = dialog.icon,
                )
            },
        )
    }
}

@Composable
private fun GoalSummaryList(
    modifier: Modifier = Modifier,
    summaryList: List<GoalSummary>,
    openedMenuGoalId: Long?,
    pendingIds: Set<Long>,
    onOpenMenu: (Long) -> Unit,
    onCloseMenu: () -> Unit,
    onEdit: (Long) -> Unit = {},
    onRequestDelete: (Long) -> Unit = {},
    onRequestEnd: (Long) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        items(summaryList, key = { it.goalId }) { item ->
            GoalSummaryItem(
                item = item,
                openedMenuGoalId = openedMenuGoalId,
                onOpenMenu = onOpenMenu,
                onCloseMenu = onCloseMenu,
                onEdit = onEdit,
                onShowDeleteDialog = onRequestDelete,
                onShowEndDialog = onRequestEnd,
                isPending = item.goalId in pendingIds,
            )
        }
    }
}

@Composable
private fun GoalSummaryItem(
    item: GoalSummary,
    openedMenuGoalId: Long?,
    onOpenMenu: (Long) -> Unit,
    onCloseMenu: () -> Unit,
    onEdit: (Long) -> Unit,
    onShowEndDialog: (Long) -> Unit,
    onShowDeleteDialog: (Long) -> Unit,
    isPending: Boolean,
) {
    val menuVisible = openedMenuGoalId == item.goalId

    GoalCardFrame(
        goalName = item.name,
        goalIcon = item.icon,
        right = {
            Box(
                modifier = Modifier.wrapContentSize(),
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_meatball),
                    contentDescription = null,
                    modifier =
                        Modifier
                            .size(24.dp)
                            .noRippleClickable(enabled = !isPending, onClick = { onOpenMenu(item.goalId) }),
                )

                CommonPopup(
                    visible = menuVisible,
                    anchorOffset = IntOffset(x = -180, y = 68),
                    onDismiss = onCloseMenu,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .width(88.dp)
                                .background(CommonColor.White, RoundedCornerShape(12.dp))
                                .border(1.dp, GrayColor.C500, RoundedCornerShape(12.dp)),
                    ) {
                        CommonPopupItem(
                            text = stringResource(R.string.action_edit),
                            onClick = {
                                onEdit(item.goalId)
                                onCloseMenu()
                            },
                        )
                        CommonPopupDivider()
                        CommonPopupItem(
                            text = stringResource(R.string.action_finish),
                            onClick = {
                                onShowEndDialog(item.goalId)
                            },
                        )
                        CommonPopupDivider()
                        CommonPopupItem(
                            text = stringResource(R.string.action_delete),
                            onClick = {
                                onShowDeleteDialog(item.goalId)
                            },
                        )
                    }
                }
            }
        },
        content = {
            HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)
            Column(
                modifier =
                    Modifier
                        .background(GrayColor.C050)
                        .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                val endDate =
                    item.endDate?.let { stringResource(R.string.date_year_month_day, it.year, it.monthValue, it.dayOfMonth) }
                        ?: stringResource(R.string.word_not_set)

                GoalSummaryInfo(
                    label = stringResource(R.string.header_repeat_type),
                    value = item.repeatCycle.label(),
                )
                GoalSummaryInfo(
                    label = stringResource(R.string.word_start_date),
                    value =
                        stringResource(
                            R.string.date_year_month_day,
                            item.startDate.year,
                            item.startDate.monthValue,
                            item.startDate.dayOfMonth,
                        ),
                )
                GoalSummaryInfo(
                    label = stringResource(R.string.word_end_date),
                    value = endDate,
                )
            }
        },
    )
}

@Composable
private fun GoalSummaryInfo(
    label: String,
    value: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(28.dp),
    ) {
        AppText(
            text = label,
            style = AppTextStyle.C1,
            color = GrayColor.C400,
            modifier = Modifier.width(48.dp),
            textAlign = TextAlign.Start,
        )

        AppText(
            text = value,
            style = AppTextStyle.B4,
            color = GrayColor.C500,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun GoalSummaryDialogContent(
    title: String = "",
    content: String = "",
    icon: GoalIconType,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(icon.toRes()),
            contentDescription = "emoji",
            modifier =
                Modifier
                    .size(60.dp),
        )

        Spacer(Modifier.height(12.dp))

        AppText(
            text = title,
            style = AppTextStyle.T1,
            color = GrayColor.C500,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(8.dp))

        AppText(
            text = content,
            style = AppTextStyle.B2,
            color = GrayColor.C500,
            textAlign = TextAlign.Center,
        )
    }
}
