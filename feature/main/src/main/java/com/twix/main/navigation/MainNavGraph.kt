package com.twix.main.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.main.MainRoute
import com.twix.navigation.NavRoutes
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
                    navigateToCertificationDetail = { goalId, date ->
                        navController.navigate(
                            NavRoutes.TaskCertificationDetailRoute.createRoute(
                                goalId,
                                date,
                            ),
                        ) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
