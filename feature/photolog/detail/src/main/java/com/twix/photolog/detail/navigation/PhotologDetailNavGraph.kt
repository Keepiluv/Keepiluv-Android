package com.twix.photolog.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.DetailNavArgs
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.detail.PhotologDetailRoute

object PhotologDetailNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.PhotologGraph
    override val startDestination: String
        get() = NavRoutes.PhotologDetailRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        composable(
            route = NavRoutes.PhotologDetailRoute.route,
            arguments =
                listOf(
                    navArgument(NavRoutes.PhotologDetailRoute.ARG_GOAL_ID) {
                        type = NavType.LongType
                    },
                    navArgument(NavRoutes.PhotologDetailRoute.ARG_DATE) {
                        type = NavType.StringType
                    },
                    navArgument(NavRoutes.PhotologDetailRoute.ARG_BETWEEN_US) {
                        type = NavType.StringType
                    },
                    navArgument(NavRoutes.PhotologDetailRoute.ARG_IS_COMPLETED) {
                        type = NavType.BoolType
                        defaultValue = false
                    },
                ),
        ) {
            PhotologDetailRoute(
                navigateToBack = navController::popBackStack,
                navigateToCertification = { goalId, date ->
                    val destination =
                        NavRoutes.PhotologRoute.createRoute(
                            DetailNavArgs(
                                goalId = goalId,
                                from = NavRoutes.PhotologRoute.From.DETAIL,
                                selectedDate = date.toString(),
                            ),
                        )
                    navController.navigate(destination)
                },
                navigateToEditor = { goalId, date ->
                    navController.navigate(
                        NavRoutes.PhotologEditorRoute.createRoute(
                            goalId = goalId,
                            date = date,
                        ),
                    )
                },
            )
        }
    }
}
