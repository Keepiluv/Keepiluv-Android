package com.twix.photolog.capture.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.capture.PhotologCaptureRoute

object PhotologCaptureNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.PhotologRoute
    override val startDestination: String
        get() = NavRoutes.PhotologRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        composable(
            route = NavRoutes.PhotologRoute.route,
            arguments =
                listOf(
                    navArgument(NavRoutes.PhotologRoute.ARG_DATA) {
                        type = NavType.StringType
                    },
                ),
        ) {
            PhotologCaptureRoute(
                navigateToBack = navController::popBackStack,
                navigateToDetail = { goalId, date, betweenUs ->
                    navController.navigate(
                        NavRoutes.PhotologDetailRoute.createRoute(
                            goalId = goalId,
                            date = date,
                            betweenUs = betweenUs.name,
                        ),
                    ) {
                        popUpTo(NavRoutes.PhotologDetailRoute.route) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
            )
        }
    }
}
