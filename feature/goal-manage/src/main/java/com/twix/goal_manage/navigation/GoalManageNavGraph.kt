package com.twix.goal_manage.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twix.goal_manage.GoalManageRoute
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import java.time.LocalDate

object GoalManageNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.GoalManageGraph
    override val startDestination: String
        get() = NavRoutes.GoalManageRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(
                route = NavRoutes.GoalManageRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.GoalManageRoute.ARG_DATE) { type = NavType.StringType },
                    ),
            ) { backStackEntry ->
                val dateStr =
                    backStackEntry.arguments
                        ?.getString(NavRoutes.GoalManageRoute.ARG_DATE)
                        ?: LocalDate.now().toString()

                val selectedDate = LocalDate.parse(dateStr)

                GoalManageRoute(
                    selectedDate = selectedDate,
                    popBackStack = { navController.popBackStack() },
                    navigateToGoalEditor = {
                        navController.navigate(NavRoutes.GoalEditorRoute.createRoute(it)) {
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
    }
}
