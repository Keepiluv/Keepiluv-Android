package com.twix.login.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.domain.model.OnboardingStatus
import com.twix.login.LoginRoute
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.navigation_contract.InviteLaunchEventSource
import org.koin.compose.koinInject

object LoginNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.LoginGraph
    override val startDestination: String
        get() = NavRoutes.LoginRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.LoginRoute.route) {
                val eventSource: InviteLaunchEventSource = koinInject()

                LoginRoute(
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(NavRoutes.LoginGraph.route) {
                                inclusive = true
                            }
                        }
                    },
                    navigateToOnBoarding = { status ->
                        val destination =
                            when (status) {
                                OnboardingStatus.COUPLE_CONNECTION -> {
                                    val pendingCode = eventSource.pendingInviteCode.value
                                    if (pendingCode != null) {
                                        eventSource.consumePendingInviteCode()
                                        NavRoutes.InviteRoute.createRoute(pendingCode)
                                    } else {
                                        NavRoutes.CoupleConnectionRoute.route
                                    }
                                }
                                OnboardingStatus.PROFILE_SETUP -> NavRoutes.ProfileRoute.route
                                OnboardingStatus.ANNIVERSARY_SETUP -> NavRoutes.DdayRoute.route
                                OnboardingStatus.COMPLETED -> return@LoginRoute
                            }

                        navController.navigate(destination)
                    },
                )
            }
        }
    }
}
