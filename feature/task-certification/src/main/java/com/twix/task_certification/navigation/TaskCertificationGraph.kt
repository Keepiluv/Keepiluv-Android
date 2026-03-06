package com.twix.task_certification.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.DetailNavArgs
import com.twix.navigation.base.NavGraphContributor
import com.twix.task_certification.certification.TaskCertificationRoute
import com.twix.task_certification.detail.TaskCertificationDetailRoute
import com.twix.task_certification.editor.TaskCertificationEditorRoute

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
                        navArgument(NavRoutes.TaskCertificationDetailRoute.ARG_IS_COMPLETED) {
                            type = NavType.BoolType
                            defaultValue = false
                        },
                    ),
            ) {
                TaskCertificationDetailRoute(
                    navigateToBack = navController::popBackStack,
                    navigateToCertification = { goalId, date ->
                        val destination =
                            NavRoutes.TaskCertificationRoute.createRoute(
                                DetailNavArgs(
                                    goalId = goalId,
                                    from = NavRoutes.TaskCertificationRoute.From.DETAIL,
                                    selectedDate = date.toString(),
                                ),
                            )
                        navController.navigate(destination)
                    },
                    navigateToEditor = { goalId, date ->
                        navController.navigate(
                            NavRoutes.TaskCertificationEditorRoute.createRoute(
                                goalId = goalId,
                                date = date,
                            ),
                        )
                    },
                )
            }

            composable(
                route = NavRoutes.TaskCertificationEditorRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.TaskCertificationEditorRoute.ARG_GOAL_ID) {
                            type = NavType.LongType
                        },
                        navArgument(NavRoutes.TaskCertificationEditorRoute.ARG_DATE) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                TaskCertificationEditorRoute(
                    navigateToBack = navController::popBackStack,
                    navigateToCertification = { goalId, photologId, comment, selectedDate ->
                        val destination =
                            NavRoutes.TaskCertificationRoute.createRoute(
                                DetailNavArgs(
                                    goalId = goalId,
                                    from = NavRoutes.TaskCertificationRoute.From.EDITOR,
                                    photologId = photologId,
                                    selectedDate = selectedDate.toString(),
                                    comment = comment,
                                ),
                            )
                        navController.navigate(destination)
                    },
                )
            }

            composable(
                route = NavRoutes.TaskCertificationRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.TaskCertificationRoute.ARG_DATA) {
                            type = NavType.StringType
                        },
                    ),
            ) {
                TaskCertificationRoute(
                    navigateToBack = navController::popBackStack,
                    navigateToDetail = { goalId, date, betweenUs ->
                        navController.navigate(
                            NavRoutes.TaskCertificationDetailRoute.createRoute(
                                goalId = goalId,
                                date = date,
                                betweenUs = betweenUs.name,
                            ),
                        ) {
                            popUpTo(NavRoutes.TaskCertificationDetailRoute.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
