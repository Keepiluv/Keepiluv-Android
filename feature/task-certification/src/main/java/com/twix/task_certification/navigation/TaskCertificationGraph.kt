package com.twix.task_certification.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.task_certification.certification.TaskCertificationRoute
import com.twix.task_certification.detail.TaskCertificationDetailRoute

object TaskCertificationGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.TaskCertificationGraph
    override val startDestination: String
        get() = NavRoutes.TaskCertificationDetailRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(
                route = NavRoutes.TaskCertificationDetailRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_GOAL_ID) {
                            type = NavType.LongType
                        },
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_DATE) {
                            type = NavType.StringType
                        },
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_BETWEEN_US) {
                            type = NavType.StringType
                        },
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_DATE) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                TaskCertificationDetailRoute(
                    navigateToBack = navController::popBackStack,
                    navigateToUpload = {
                        val destination =
                            NavRoutes.TaskCertificationRoute.createRoute(
                                goalId = it,
                                from = NavRoutes.TaskCertificationRoute.From.DETAIL,
                            )
                        navController.navigate(destination)
                    },
                    navigateToEditor = { },
                )
            }

            composable(
                route = NavRoutes.TaskCertificationRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.TaskCertificationRoute.ARG_GOAL_ID) {
                            type = NavType.LongType
                        },
                        navArgument(NavRoutes.TaskCertificationRoute.ARG_FROM) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                TaskCertificationRoute(
                    navigateToBack = {
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
