package com.yapp.stats.detail.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.yapp.stats.detail.StatsDetailRoute

object StatsDetailGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.StatsDetailGraph

    override val startDestination: String
        get() = NavRoutes.StatsDetailRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        composable(
            route = NavRoutes.StatsDetailRoute.route,
            arguments =
                listOf(
                    navArgument(NavRoutes.StatsDetailRoute.ARG_GOAL_ID) {
                        type = NavType.LongType
                    },
                    navArgument(NavRoutes.StatsDetailRoute.ARG_DATE) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                ),
        ) {
            StatsDetailRoute(
                onBack = navController::popBackStack,
            )
        }
    }
}
