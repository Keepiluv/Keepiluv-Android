package com.twix.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.splash.SplashRoute

object SplashNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.SplashGraph
    override val startDestination: String
        get() = NavRoutes.SplashRoute.route
    override val priority: Int
        get() = 0

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.SplashRoute.route) {
                SplashRoute(
                    navigateToMain = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.SplashGraph.route) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    },
                    navigateToLogin = {
                        navController.navigate(NavRoutes.LoginGraph.route) {
                            popUpTo(NavRoutes.SplashGraph.route) {
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
