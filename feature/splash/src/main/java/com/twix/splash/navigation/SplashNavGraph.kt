package com.twix.splash.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.domain.model.OnboardingStatus
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.navigation_contract.InviteLaunchEventSource
import com.twix.splash.SplashRoute
import org.koin.compose.koinInject

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
                val inviteLaunchEventSource: InviteLaunchEventSource = koinInject()

                SplashRoute(
                    navigateToMain = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.SplashGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToLogin = {
                        navController.navigate(NavRoutes.LoginGraph.route) {
                            popUpTo(NavRoutes.SplashGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    navigateToOnBoarding = { status ->
                        val destination = when (status) {
                            OnboardingStatus.COUPLE_CONNECTION -> {
                                val pendingCode = inviteLaunchEventSource.pendingInviteCode.value
                                if (pendingCode != null) {
                                    inviteLaunchEventSource.consumePendingInviteCode()
                                    NavRoutes.InviteRoute.createRoute(pendingCode)
                                } else {
                                    NavRoutes.CoupleConnectionRoute.route
                                }
                            }
                            OnboardingStatus.PROFILE_SETUP -> NavRoutes.ProfileRoute.route
                            OnboardingStatus.ANNIVERSARY_SETUP -> NavRoutes.DdayRoute.route
                            OnboardingStatus.COMPLETED -> return@SplashRoute
                        }

                        navController.navigate(NavRoutes.OnboardingGraph.route) {
                            popUpTo(NavRoutes.SplashGraph.route) { inclusive = true }
                            launchSingleTop = true
                        }
                        navController.navigate(destination)
                    },
                )
            }
        }
    }
}
