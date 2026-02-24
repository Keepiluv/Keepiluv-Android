package com.twix.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.components.bottomsheet.CommonBottomSheet
import com.twix.designsystem.components.bottomsheet.model.CommonBottomSheetConfig
import com.twix.designsystem.components.calendar.Calendar
import com.twix.designsystem.theme.CommonColor
import com.twix.domain.model.enums.BetweenUs
import com.twix.home.HomeIntent
import com.twix.home.HomeRoute
import com.twix.home.HomeViewModel
import com.twix.main.component.MainBottomBar
import com.twix.main.model.MainTab
import org.koin.androidx.compose.koinViewModel
import java.time.LocalDate

@Composable
fun MainRoute(
    viewModel: MainViewModel = koinViewModel(),
    navigateToGoalEditor: () -> Unit,
    navigateToGoalManage: (LocalDate) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToCertificationDetail: (Long, LocalDate, BetweenUs) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val homeViewModel: HomeViewModel = koinViewModel()

    MainScreen(
        homeViewModel = homeViewModel,
        selectedTab = uiState.selectedTab,
        onTabClick = { tab -> viewModel.dispatch(MainIntent.SelectTab(tab)) },
        navigateToGoalEditor = navigateToGoalEditor,
        navigateToGoalManage = navigateToGoalManage,
        navigateToCertificationDetail = navigateToCertificationDetail,
        navigateToCertification = navigateToCertification,
        navigateToSettings = navigateToSettings,
    )
}

@Composable
private fun MainScreen(
    homeViewModel: HomeViewModel,
    selectedTab: MainTab,
    onTabClick: (MainTab) -> Unit,
    navigateToGoalEditor: () -> Unit,
    navigateToGoalManage: (LocalDate) -> Unit,
    navigateToSettings: () -> Unit,
    navigateToCertification: (Long, LocalDate) -> Unit,
    navigateToCertificationDetail: (Long, LocalDate, BetweenUs) -> Unit,
) {
    val calendarState by homeViewModel.calendarState.collectAsStateWithLifecycle()
    var showCalendarBottomSheet by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Scaffold(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(CommonColor.White),
            bottomBar = {
                MainBottomBar(
                    selectedTab = selectedTab,
                    onTabClick = onTabClick,
                )
            },
        ) { padding ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(CommonColor.White)
                        .padding(padding),
            ) {
                when (selectedTab) {
                    MainTab.HOME ->
                        HomeRoute(
                            viewModel = homeViewModel,
                            onShowCalendarBottomSheet = { showCalendarBottomSheet = true },
                            navigateToGoalEditor = navigateToGoalEditor,
                            navigateToGoalManage = navigateToGoalManage,
                            navigateToCertificationDetail = navigateToCertificationDetail,
                            navigateToSettings = navigateToSettings,
                            navigateToCertification = navigateToCertification,
                        )

                    MainTab.STATS -> Box(modifier = Modifier.fillMaxSize())
                    MainTab.COUPLE -> Box(modifier = Modifier.fillMaxSize())
                }
            }
        }

        CommonBottomSheet(
            visible = showCalendarBottomSheet,
            config = CommonBottomSheetConfig(showHandle = false),
            onDismissRequest = { showCalendarBottomSheet = false },
            content = {
                Calendar(
                    initialDate = calendarState.selectedDate,
                    onComplete = {
                        homeViewModel.dispatch(HomeIntent.SelectDate(it))
                        showCalendarBottomSheet = false
                    },
                )
            },
        )
    }
}
