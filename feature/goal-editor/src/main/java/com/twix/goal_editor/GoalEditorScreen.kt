package com.twix.goal_editor

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.bottomsheet.CommonBottomSheet
import com.twix.designsystem.components.bottomsheet.model.CommonBottomSheetConfig
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.calendar.Calendar
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.extension.label
import com.twix.designsystem.extension.toRes
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.goal_editor.component.EmojiPicker
import com.twix.goal_editor.component.GoalInfoCard
import com.twix.goal_editor.component.GoalTextField
import com.twix.goal_editor.model.GoalEditorUiState
import com.twix.ui.extension.dismissKeyboardOnTap
import com.twix.ui.extension.noRippleClickable
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun GoalEditorRoute(
    viewModel: GoalEditorViewModel = koinViewModel(),
    toastManager: ToastManager = koinInject(),
    goalId: Long,
    navigateToBack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    /**
     * LaunchedEffect 내부에서는 stringResource를 사용할 수 없고, context.getString을 그대로 사용하면 경고 발생
     * LaunchedEffect가 예전 context를 들고 있지 않도록 막아주는 용도
     * */
    val currentContext by rememberUpdatedState(context)

    LaunchedEffect(Unit) {
        viewModel.sideEffect.collect { effect ->
            when (effect) {
                is GoalEditorSideEffect.ShowToast -> toastManager.tryShow(ToastData(currentContext.getString(effect.resId), effect.type))
                is GoalEditorSideEffect.NavigateToHome -> navigateToBack()
            }
        }
    }

    LaunchedEffect(goalId) {
        if (goalId != -1L) viewModel.dispatch(GoalEditorIntent.InitGoal(goalId))
    }

    GoalEditorScreen(
        uiState = uiState,
        isEdit = goalId != -1L,
        onBack = navigateToBack,
        onCommitTitle = { viewModel.dispatch(GoalEditorIntent.SetTitle(it)) },
        onSelectRepeatType = { viewModel.dispatch(GoalEditorIntent.SetRepeatType(it)) },
        onCommitIcon = { viewModel.dispatch(GoalEditorIntent.SetIcon(it)) },
        onCommitEndDate = { viewModel.dispatch(GoalEditorIntent.SetEndDate(it)) },
        onCommitStartDate = { viewModel.dispatch(GoalEditorIntent.SetStartDate(it)) },
        onCommitRepeatCount = { viewModel.dispatch(GoalEditorIntent.SetRepeatCount(it)) },
        onToggleEndDateEnabled = { viewModel.dispatch(GoalEditorIntent.SetEndDateEnabled(it)) },
        onComplete = { viewModel.dispatch(GoalEditorIntent.Save(goalId)) },
    )
}

@Composable
fun GoalEditorScreen(
    uiState: GoalEditorUiState,
    isEdit: Boolean = false,
    onBack: () -> Unit,
    onCommitTitle: (String) -> Unit,
    onSelectRepeatType: (RepeatCycle) -> Unit,
    onCommitIcon: (GoalIconType) -> Unit,
    onCommitEndDate: (LocalDate) -> Unit,
    onCommitStartDate: (LocalDate) -> Unit,
    onCommitRepeatCount: (Int) -> Unit,
    onToggleEndDateEnabled: (Boolean) -> Unit,
    onComplete: () -> Unit,
) {
    var showRepeatCountBottomSheet by remember { mutableStateOf(false) }
    var showCalendarBottomSheet by remember { mutableStateOf(false) }
    var showIconEditorDialog by remember { mutableStateOf(false) }
    var isEndDate by remember { mutableStateOf(true) }
    var internalSelectedIcon by remember { mutableStateOf(uiState.selectedIcon) }

    Box {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(CommonColor.White)
                    .dismissKeyboardOnTap(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CommonTopBar(
                title = if (isEdit) stringResource(R.string.goal_editor_title_edit) else stringResource(R.string.goal_editor_title),
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

            Spacer(Modifier.height(52.dp))

            EmojiPicker(
                icon = uiState.selectedIcon,
                onClick = { showIconEditorDialog = true },
            )

            Spacer(Modifier.height(44.dp))

            GoalTextField(
                value = uiState.goalTitle,
                onCommitTitle = onCommitTitle,
            )

            Spacer(Modifier.height(44.dp))

            GoalInfoCard(
                selectedRepeatCycle = uiState.selectedRepeatCycle,
                repeatCount = uiState.repeatCount,
                startDate = uiState.startDate,
                endDateEnabled = uiState.endDateEnabled,
                endDate = uiState.endDate,
                isEdit = isEdit,
                onSelectedRepeatType = onSelectRepeatType,
                onShowRepeatCountBottomSheet = { showRepeatCountBottomSheet = true },
                onShowCalendarBottomSheet = {
                    isEndDate = it
                    showCalendarBottomSheet = true
                },
                onToggleEndDateEnabled = onToggleEndDateEnabled,
            )

            Spacer(Modifier.weight(1f))

            AppButton(
                onClick = onComplete,
                modifier =
                    Modifier
                        .padding(horizontal = 20.dp)
                        .padding(vertical = 8.dp)
                        .fillMaxWidth(),
                text = stringResource(R.string.word_completion),
            )
        }

        CommonBottomSheet(
            visible = showCalendarBottomSheet,
            config = CommonBottomSheetConfig(showHandle = false),
            onDismissRequest = { showCalendarBottomSheet = false },
            content = {
                Calendar(
                    initialDate = if (isEndDate) uiState.endDate else uiState.startDate,
                    onComplete = {
                        if (isEndDate) onCommitEndDate(it) else onCommitStartDate(it)
                        showCalendarBottomSheet = false
                    },
                )
            },
        )

        CommonBottomSheet(
            visible = showRepeatCountBottomSheet,
            config = CommonBottomSheetConfig(showHandle = false),
            onDismissRequest = { showRepeatCountBottomSheet = false },
            content = {
                RepeatCountBottomSheetContent(
                    repeatCount = uiState.repeatCount,
                    selectedRepeatCycle = uiState.selectedRepeatCycle,
                    onCommit = { repeatType, repeatCount ->
                        onSelectRepeatType(repeatType)
                        onCommitRepeatCount(repeatCount)
                        showRepeatCountBottomSheet = false
                    },
                )
            },
        )

        CommonDialog(
            visible = showIconEditorDialog,
            confirmText = stringResource(R.string.word_completion),
            onDismissRequest = { showIconEditorDialog = false },
            onConfirm = {
                onCommitIcon(internalSelectedIcon)
                showIconEditorDialog = false
            },
            content = {
                IconEditorDialogContent(
                    selectedIcon = internalSelectedIcon,
                    onClick = { internalSelectedIcon = it },
                )
            },
        )
    }
}

@Composable
private fun IconEditorDialogContent(
    selectedIcon: GoalIconType,
    onClick: (GoalIconType) -> Unit,
) {
    AppText(
        text = stringResource(R.string.icon_editor_dialog_title),
        style = AppTextStyle.T1,
        color = GrayColor.C500,
    )

    Spacer(Modifier.height(16.dp))

    LazyVerticalGrid(
        columns = GridCells.Fixed(4),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(GoalIconType.entries) {
            val isSelected = it == selectedIcon

            /**
             * LazyVerticalGrid(GridCells.Fixed(4))는 각 아이템을 전체 폭을 4등분한 값으로 측정함
             * 따라서 아이템 루트에 CircleShape Border를 적용해도 LazyVerticalGrid가 측정한 아이템 폭에 의해 타원처럼 보일 수 있음
             *
             * 그래서 바깥 Box가 LazyVerticalGrid가 측정한 폭을 받아 정렬만 담당하고,
             * 실제 원형 영역은 안쪽 Box(size + CircleShape)로 적용함
             */
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .border(1.dp, if (isSelected) GrayColor.C500 else GrayColor.C100, CircleShape)
                            .noRippleClickable(onClick = { onClick(it) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(it.toRes()),
                        contentDescription = "emoji",
                        modifier =
                            Modifier
                                .size(42.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RepeatCountBottomSheetContent(
    repeatCount: Int,
    selectedRepeatCycle: RepeatCycle,
    onCommit: (RepeatCycle, Int) -> Unit,
) {
    var internalRepeatCount by remember { mutableIntStateOf(repeatCount) }
    var internalSelectedRepeatType by remember { mutableStateOf(selectedRepeatCycle) }
    val maxCount = if (internalSelectedRepeatType == RepeatCycle.WEEKLY) 6 else 25

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            AppText(
                text = RepeatCycle.WEEKLY.label(),
                style = AppTextStyle.B2,
                color = if (internalSelectedRepeatType == RepeatCycle.WEEKLY) CommonColor.White else GrayColor.C500,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (internalSelectedRepeatType == RepeatCycle.WEEKLY) GrayColor.C500 else CommonColor.White)
                        .border(1.dp, GrayColor.C500, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.5.dp)
                        .noRippleClickable(onClick = {
                            internalSelectedRepeatType = RepeatCycle.WEEKLY
                            internalRepeatCount = 1
                        }),
            )

            AppText(
                text = RepeatCycle.MONTHLY.label(),
                style = AppTextStyle.B2,
                color = if (internalSelectedRepeatType == RepeatCycle.MONTHLY) CommonColor.White else GrayColor.C500,
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (internalSelectedRepeatType == RepeatCycle.MONTHLY) GrayColor.C500 else CommonColor.White)
                        .border(1.dp, GrayColor.C500, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 5.5.dp)
                        .noRippleClickable(onClick = {
                            internalSelectedRepeatType = RepeatCycle.MONTHLY
                            internalRepeatCount = 1
                        }),
            )
        }

        Spacer(Modifier.height(36.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_minus),
                contentDescription = "minus",
                colorFilter = ColorFilter.tint(CommonColor.White),
                modifier =
                    Modifier
                        .background(GrayColor.C500, CircleShape)
                        .padding(4.dp)
                        .size(28.dp)
                        .noRippleClickable(onClick = { if (internalRepeatCount > 1) internalRepeatCount-- }),
            )

            Row(
                modifier =
                    Modifier
                        .width(96.dp)
                        .border(1.dp, GrayColor.C300, RoundedCornerShape(12.dp))
                        .padding(horizontal = 27.5.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
            ) {
                AppText(
                    text = internalRepeatCount.toString(),
                    style = AppTextStyle.H2,
                    color = GrayColor.C500,
                )

                Spacer(Modifier.width(8.dp))

                Box(
                    modifier =
                        Modifier
                            .size(24.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    AppText(
                        text = stringResource(R.string.word_count),
                        style = AppTextStyle.T2,
                        color = GrayColor.C300,
                    )
                }
            }

            Image(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = "plus",
                colorFilter = ColorFilter.tint(CommonColor.White),
                modifier =
                    Modifier
                        .background(GrayColor.C500, CircleShape)
                        .padding(4.dp)
                        .size(28.dp)
                        .noRippleClickable(onClick = { if (maxCount > internalRepeatCount) internalRepeatCount++ }),
            )
        }

        Spacer(Modifier.height(32.dp))

        AppButton(
            onClick = { onCommit(internalSelectedRepeatType, internalRepeatCount) },
            modifier =
                Modifier
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .fillMaxWidth(),
            text = stringResource(R.string.word_completion),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TwixTheme {
        val uiState = GoalEditorUiState()

        GoalEditorScreen(
            uiState = uiState,
            onBack = {},
            onCommitTitle = {},
            onSelectRepeatType = {},
            onCommitEndDate = {},
            onCommitStartDate = {},
            onCommitRepeatCount = {},
            onToggleEndDateEnabled = {},
            onComplete = {},
            onCommitIcon = {},
            isEdit = false,
        )
    }
}
