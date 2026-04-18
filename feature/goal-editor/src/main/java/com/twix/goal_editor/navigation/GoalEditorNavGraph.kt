package com.twix.goal_editor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twix.goal_editor.GoalEditorRoute
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor

object GoalEditorNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.GoalEditorGraph
    override val startDestination: String
        get() = NavRoutes.GoalEditorRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(
                route = NavRoutes.GoalEditorRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.GoalEditorRoute.ARG_ID) { defaultValue = -1L },
                    ),
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong(NavRoutes.GoalEditorRoute.ARG_ID) ?: -1L

                GoalEditorRoute(
                    goalId = id,
                    navigateToBack = navController::popBackStack,
                )
            }
        }
    }
}
