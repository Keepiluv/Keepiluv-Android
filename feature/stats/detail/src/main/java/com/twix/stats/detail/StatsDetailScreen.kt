package com.twix.stats.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.calendar.CalendarNavigator
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.error.ErrorScreen
import com.twix.designsystem.components.loading.TwixLoadingOverlay
import com.twix.designsystem.components.stats.StatsCalendar
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.extension.toResId
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalIconType
import com.twix.stats.detail.component.StatsDetailTopbar
import com.twix.stats.detail.component.SummaryContent
import com.twix.stats.detail.contract.StatsDetailSideEffect
import com.twix.stats.detail.contract.StatsDetailUiState
import com.twix.stats.detail.preview.StatsDetailUiStatePreviewProvider
import com.twix.ui.base.ObserveAsEvents
import com.yapp.stats.detail.contract.StatsDetailIntent
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import java.time.LocalDate

@Composable
fun StatsDetailRoute(
    onBack: () -> Unit,
    navigateToGoalEditor: (Long) -> Unit,
    navigateToPhotologDetail: (Long, LocalDate, BetweenUs, Boolean) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: StatsDetailViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            StatsDetailSideEffect.NavigateToBack -> onBack()
            is StatsDetailSideEffect.NavigateToGoalEditor -> navigateToGoalEditor(sideEffect.goalId)
            is StatsDetailSideEffect.NavigateToPhotologDetail ->
                navigateToPhotologDetail(
                    sideEffect.goalId,
                    sideEffect.date,
                    sideEffect.betweenUs,
                    sideEffect.isCompleted,
                )
            is StatsDetailSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(sideEffect.message),
                        type = sideEffect.type,
                    ),
                )
            }
        }
    }

    StatsDetailScreen(
        uiState = uiState,
        onBack = onBack,
        onRetry = { viewModel.dispatch(StatsDetailIntent.Retry) },
        onSelectDate = { selectedDate -> viewModel.dispatch(StatsDetailIntent.SelectDate(selectedDate)) },
        onPreviousMonth = { viewModel.dispatch(StatsDetailIntent.PreviousMonth) },
        onNextMonth = { viewModel.dispatch(StatsDetailIntent.NextMonth) },
        onClickDeleteStats = { viewModel.dispatch(StatsDetailIntent.GoalDelete) },
        onClickPopupEdit = { viewModel.dispatch(StatsDetailIntent.GoalEdit) },
        onClickPopupEnd = { viewModel.dispatch(StatsDetailIntent.GoalEnd) },
    )
}

@Composable
fun StatsDetailScreen(
    uiState: StatsDetailUiState,
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onSelectDate: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onClickDeleteStats: () -> Unit,
    onClickPopupEdit: () -> Unit,
    onClickPopupEnd: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var popupMenuVisibility by remember { mutableStateOf(false) }
    var statsDeleteDialogVisibility by remember { mutableStateOf(false) }
    val isInProgressStatsDetail = !uiState.detail.isCompleted

    Box {
        when {
            uiState.showLoading -> {
                TwixLoadingOverlay(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .background(GrayColor.C050),
                )
            }

            uiState.showError -> {
                ErrorScreen(
                    onClickRetry = onRetry,
                    onClickBack = onBack,
                    modifier = Modifier.background(GrayColor.C050),
                )
            }

            else -> {
                Column(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .background(GrayColor.C050),
                ) {
                    StatsDetailTopbar(
                        goalName = uiState.detail.goalName,
                        isInProgressStatsDetail = isInProgressStatsDetail,
                        popupMenuVisibility = popupMenuVisibility,
                        onBack = onBack,
                        onClickAction = {
                            if (isInProgressStatsDetail) {
                                popupMenuVisibility = true
                            } else {
                                statsDeleteDialogVisibility = true
                            }
                        },
                        onDismiss = { popupMenuVisibility = false },
                        onClickPopupEdit = {
                            popupMenuVisibility = false
                            onClickPopupEdit()
                        },
                        onClickPopupEnd = {
                            popupMenuVisibility = false
                            onClickPopupEnd()
                        },
                        onClickPopupDelete = {
                            popupMenuVisibility = false
                            statsDeleteDialogVisibility = true
                        },
                    )

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(top = 32.dp),
                    ) {
                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_hug),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .align(Alignment.TopStart)
                                    .padding(start = 20.dp),
                        )

                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp)
                                    .padding(top = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            CalendarNavigator(
                                currentDate = uiState.detail.currentDate,
                                onPreviousMonth = onPreviousMonth,
                                onNextMonth = onNextMonth,
                                hasPrevious = uiState.hasPrevious,
                                hasNext = uiState.hasNext,
                            )

                            Box(
                                Modifier
                                    .fillMaxWidth()
                                    .background(CommonColor.White, shape = RoundedCornerShape(16.dp))
                                    .border(
                                        color = GrayColor.C500,
                                        width = 1.dp,
                                        shape = RoundedCornerShape(16.dp),
                                    ).padding(horizontal = 12.dp)
                                    .padding(top = 24.dp, bottom = 32.dp),
                            ) {
                                StatsCalendar(
                                    uiModel = uiState.calendarUiModel,
                                    onSelectedDate = onSelectDate,
                                )
                            }
                        }

                        Image(
                            imageVector = ImageVector.vectorResource(R.drawable.ic_plane),
                            contentDescription = null,
                            modifier =
                                Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(end = 27.dp),
                        )
                    }

                    Spacer(Modifier.height(44.dp))

                    SummaryContent(uiState.summary)
                }

                CommonDialog(
                    visible = statsDeleteDialogVisibility,
                    confirmText = stringResource(R.string.word_delete),
                    dismissText = stringResource(R.string.word_cancel),
                    onDismissRequest = { statsDeleteDialogVisibility = false },
                    onConfirm = {
                        statsDeleteDialogVisibility = false
                        onClickDeleteStats()
                    },
                    onDismiss = { statsDeleteDialogVisibility = false },
                    content = {
                        StatsDeleteDialogContent(
                            title =
                                stringResource(
                                    R.string.dialog_delete_goal_title,
                                    uiState.detail.goalName,
                                ),
                            content = stringResource(R.string.dialog_delete_goal_content),
                            icon = uiState.detail.goalIcon,
                        )
                    },
                )

                if (uiState.showOverlayLoading) {
                    TwixLoadingOverlay(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .background(GrayColor.C050.copy(alpha = 0.7f)),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsDeleteDialogContent(
    title: String,
    content: String,
    icon: GoalIconType,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(icon.toResId()),
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

@Preview(showBackground = true)
@Composable
private fun StatsDetailScreenPreview(
    @PreviewParameter(StatsDetailUiStatePreviewProvider::class)
    uiState: StatsDetailUiState,
) {
    TwixTheme {
        StatsDetailScreen(
            uiState = uiState,
            onBack = {},
            onRetry = {},
            onSelectDate = {},
            onPreviousMonth = {},
            onNextMonth = {},
            onClickDeleteStats = {},
            onClickPopupEdit = {},
            onClickPopupEnd = {},
        )
    }
}
