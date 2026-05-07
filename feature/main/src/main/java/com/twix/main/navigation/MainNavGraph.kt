package com.twix.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.main.MainRoute
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.DetailNavArgs
import com.twix.navigation.base.NavGraphContributor

object MainNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.MainGraph
    override val startDestination: String
        get() = NavRoutes.MainRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.MainRoute.route) {
                MainRoute(
                    navigateToGoalEditor = {
                        navController.navigate(NavRoutes.GoalEditorRoute.createRoute(-1L)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToGoalEditorWithPreset = { presetId ->
                        navController.navigate(NavRoutes.GoalEditorRoute.createRoute(-1L, presetId)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToGoalManage = {
                        navController.navigate(NavRoutes.GoalManageRoute.createRoute(it)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToSettings = {
                        navController.navigate(NavRoutes.SettingsRoute.route) {
                            launchSingleTop = true
                        }
                    },
                    navigateToCertification = { goalId, date ->
                        val destination =
                            NavRoutes.PhotologRoute.createRoute(
                                DetailNavArgs(
                                    goalId = goalId,
                                    from = NavRoutes.PhotologRoute.From.HOME,
                                    selectedDate = date.toString(),
                                ),
                            )
                        navController.navigate(destination) {
                            launchSingleTop = true
                        }
                    },
                    navigateToCertificationDetail = { goalId, date, betweenUs ->
                        val destination =
                            NavRoutes.PhotologDetailRoute.createRoute(
                                goalId,
                                date,
                                betweenUs.name,
                            )
                        navController.navigate(destination) {
                            launchSingleTop = true
                        }
                    },
                    navigateToStatsDetail = { goalId, date ->
                        val destination = NavRoutes.StatsDetailRoute.createRoute(goalId, date)
                        navController.navigate(destination) {
                            launchSingleTop = true
                        }
                    },
                    navigateToNotification = {
                        navController.navigate(NavRoutes.NotificationRoute.route) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
