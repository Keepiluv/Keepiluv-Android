package com.twix.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.NotificationType
import com.twix.domain.model.notification.Notification
import com.twix.notification.component.NotificationList
import com.twix.notification.contract.NotificationIntent
import com.twix.notification.contract.NotificationSideEffect
import com.twix.notification.contract.NotificationUiState
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun NotificationRoute(
    viewModel: NotificationViewModel = koinViewModel(),
    toastManager: ToastManager = koinInject(),
    popBackStack: () -> Unit,
    navigateToMyPhotolog: (Long, LocalDate) -> Unit,
    navigateToPartnerPhotolog: (Long, LocalDate) -> Unit,
    navigateToStatisticsEndedGoals: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    ObserveAsEvents(viewModel.sideEffect) { effect ->
        when (effect) {
            is NotificationSideEffect.NavigateToHome -> popBackStack()
            is NotificationSideEffect.NavigateToMyPhotolog -> navigateToMyPhotolog(effect.goalId, effect.date)
            is NotificationSideEffect.NavigateToPartnerPhotolog -> navigateToPartnerPhotolog(effect.goalId, effect.date)
            is NotificationSideEffect.NavigateToStatisticsEndedGoals -> navigateToStatisticsEndedGoals()
            is NotificationSideEffect.ShowToast -> {
                toastManager.show(ToastData(currentContext.getString(effect.resId), effect.type))
            }
        }
    }

    NotificationScreen(
        uiState = uiState,
        onBack = popBackStack,
        onNotificationClick = { viewModel.dispatch(NotificationIntent.NotificationClicked(it)) },
        onNextPage = { viewModel.dispatch(NotificationIntent.FetchNextPage) },
    )
}

@Composable
private fun NotificationScreen(
    uiState: NotificationUiState,
    onBack: () -> Unit,
    onNotificationClick: (Long) -> Unit,
    onNextPage: () -> Unit,
) {
    val listState = rememberLazyListState()

    LaunchedEffect(listState, uiState.notificationList.size) {
        snapshotFlow {
            val layoutInfo = listState.layoutInfo
            val totalCount = layoutInfo.totalItemsCount
            val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: -1

            // 마지막 3개쯤 보일 때 미리 조회
            totalCount > 0 && lastVisibleIndex >= totalCount - 3
        }.distinctUntilChanged()
            .filter { it }
            .collect {
                if (uiState.hasNext && !uiState.isLoading) {
                    onNextPage()
                }
            }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        CommonTopBar(
            title = stringResource(R.string.word_notification),
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

        Spacer(Modifier.height(12.dp))

        AppText(
            text = stringResource(R.string.notification_recent_14_days),
            style = AppTextStyle.T1,
            color = GrayColor.C500,
            modifier =
                Modifier.padding(start = 20.dp),
        )

        Spacer(Modifier.height(4.dp))

        NotificationList(
            modifier =
                Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            notificationsList = uiState.notificationList,
            listState = listState,
            onNotificationClick = onNotificationClick,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        NotificationScreen(
            uiState =
                NotificationUiState(
                    notificationList =
                        listOf(
                            Notification(
                                id = 1,
                                title = "titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle",
                                body = "content",
                                type = NotificationType.GOAL_COMPLETED,
                                isRead = false,
                                deepLink = null,
                                createdAt = null,
                            ),
                            Notification(
                                id = 2,
                                title = "titletitletitletitletitletitletitletitletitletitle",
                                body = "content",
                                type = NotificationType.GOAL_COMPLETED,
                                isRead = false,
                                deepLink = null,
                                createdAt = null,
                            ),
                        ),
                ),
            onBack = {},
            onNotificationClick = {},
            onNextPage = {},
        )
    }
}
