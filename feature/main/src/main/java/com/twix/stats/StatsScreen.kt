package com.twix.stats

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.topbar.TitleTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.stats.component.EndStatsContent
import com.twix.stats.component.InProgressStatsContent
import com.twix.stats.contract.StatsIntent
import com.twix.stats.contract.StatsSideEffect
import com.twix.stats.contract.StatsUiState
import com.twix.stats.model.StatsTabDestination
import com.twix.stats.preview.StatsUiStatePreviewProvider
import com.twix.ui.base.ObserveAsEvents
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun StatsRoute(
    navigateToDetail: (Long, LocalDate?) -> Unit,
    toastManager: ToastManager = koinInject(),
    viewModel: StatsViewModel = koinViewModel(),
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is StatsSideEffect.ShowToast ->
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(sideEffect.message),
                        type = sideEffect.type,
                    ),
                )
        }
    }

    StatsScreen(
        uiState = uiState,
        onClickInProgressPreviousMonth = { viewModel.dispatch(StatsIntent.PreviousMonth) },
        onClickInProgressNextMonth = { viewModel.dispatch(StatsIntent.NextMonth) },
        onClickStatsCard = { goalId, destination ->
            val currentDate =
                when (destination) {
                    StatsTabDestination.IN_PROGRESS -> uiState.inProgressStats.selectedDate
                    StatsTabDestination.END -> null
                }

            navigateToDetail(goalId, currentDate)
        },
    )
}

@Composable
fun StatsScreen(
    uiState: StatsUiState,
    onClickInProgressPreviousMonth: () -> Unit,
    onClickInProgressNextMonth: () -> Unit,
    onClickStatsCard: (Long, StatsTabDestination) -> Unit,
) {
    val pagerState =
        rememberPagerState(initialPage = StatsTabDestination.IN_PROGRESS.ordinal) {
            StatsTabDestination.entries.size
        }

    Column(
        Modifier.fillMaxSize(),
    ) {
        TitleTopBar(title = stringResource(R.string.stats_top_bar_title))
        StatsTabRow(pagerState)
        StatsTabPager(
            uiState = uiState,
            pagerState = pagerState,
            onClickPreviousMonth = onClickInProgressPreviousMonth,
            onClickNextMonth = onClickInProgressNextMonth,
            onClickStatsCard = onClickStatsCard,
        )
    }
}

@Composable
private fun StatsTabRow(pagerState: PagerState) {
    val coroutineScope = rememberCoroutineScope()

    PrimaryTabRow(
        selectedTabIndex = pagerState.currentPage,
        indicator = {
            TabRowDefaults.PrimaryIndicator(
                modifier =
                    Modifier
                        .tabIndicatorOffset(pagerState.currentPage)
                        .fillMaxWidth()
                        .height(1.4.dp)
                        .padding(horizontal = 20.dp)
                        .background(GrayColor.C500),
                color = GrayColor.C500,
            )
        },
        containerColor = CommonColor.White,
    ) {
        StatsTabDestination.entries.forEachIndexed { index, destination ->
            val isSelected = pagerState.currentPage == index
            Tab(
                text = {
                    AppText(
                        text = stringResource(destination.label),
                        style = AppTextStyle.T2,
                        color = if (isSelected) GrayColor.C500 else GrayColor.C200,
                    )
                },
                selected = isSelected,
                interactionSource = noRippleInteractionSource,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.height(36.dp),
            )
        }
    }
}

@Composable
private fun StatsTabPager(
    uiState: StatsUiState,
    pagerState: PagerState,
    onClickPreviousMonth: () -> Unit,
    onClickNextMonth: () -> Unit,
    onClickStatsCard: (Long, StatsTabDestination) -> Unit,
) {
    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
    ) { page ->
        when (val tab = StatsTabDestination.entries[page]) {
            StatsTabDestination.IN_PROGRESS ->
                InProgressStatsContent(
                    currentDate = uiState.currentDate,
                    stats = uiState.inProgressStats,
                    onClickPreviousMonth = { onClickPreviousMonth() },
                    onClickNextMonth = { onClickNextMonth() },
                    onClickStatsCard = {
                        onClickStatsCard(it, tab)
                    },
                )

            StatsTabDestination.END ->
                EndStatsContent(
                    statsGoals = uiState.endStats,
                    onClickStatsCard = { onClickStatsCard(it, tab) },
                )
        }
    }
}

private val noRippleInteractionSource =
    object : MutableInteractionSource {
        override val interactions: Flow<Interaction> = emptyFlow()

        override suspend fun emit(interaction: Interaction) {}

        override fun tryEmit(interaction: Interaction) = true
    }

@Preview
@Composable
fun StatsRoutePreview(
    @PreviewParameter(StatsUiStatePreviewProvider::class)
    uiState: StatsUiState,
) {
    TwixTheme {
        StatsScreen(
            uiState = uiState,
            onClickInProgressPreviousMonth = {},
            onClickInProgressNextMonth = {},
            onClickStatsCard = { _, _ -> },
        )
    }
}
