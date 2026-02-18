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
                            defaultValue = -1L
                        },
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_DATE) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                TaskCertificationDetailRoute(
                    navigateToBack = navController::popBackStack,
                    navigateToUpload = { goalId ->
                        val destination = NavRoutes.TaskCertificationRoute.createRoute(goalId)
                        navController.navigate(destination)
                    },
                )
            }

            composable(
                route = NavRoutes.TaskCertificationRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.TaskCertificationRoute.ARG_GOAL_ID) {
                            defaultValue = -1L
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
