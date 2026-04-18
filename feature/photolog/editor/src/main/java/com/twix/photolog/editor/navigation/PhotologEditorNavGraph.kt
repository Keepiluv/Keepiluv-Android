package com.twix.photolog.editor.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.twix.navigation.NavRoutes
import com.twix.navigation.args.DetailNavArgs
import com.twix.navigation.base.NavGraphContributor
import com.twix.photolog.editor.PhotologEditorRoute

object PhotologEditorNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.PhotologEditorRoute
    override val startDestination: String
        get() = NavRoutes.PhotologEditorRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        composable(
            route = NavRoutes.PhotologEditorRoute.route,
            arguments =
                listOf(
                    navArgument(NavRoutes.PhotologEditorRoute.ARG_GOAL_ID) {
                        type = NavType.LongType
                    },
                    navArgument(NavRoutes.PhotologEditorRoute.ARG_DATE) {
                        type = NavType.StringType
                    },
                ),
        ) {
            PhotologEditorRoute(
                navigateToBack = navController::popBackStack,
                navigateToCertification = { goalId, photologId, comment, selectedDate ->
                    val destination =
                        NavRoutes.PhotologRoute.createRoute(
                            DetailNavArgs(
                                goalId = goalId,
                                from = NavRoutes.PhotologRoute.From.EDITOR,
                                photologId = photologId,
                                selectedDate = selectedDate.toString(),
                                comment = comment,
                            ),
                        )
                    navController.navigate(destination)
                },
            )
        }
    }
}
