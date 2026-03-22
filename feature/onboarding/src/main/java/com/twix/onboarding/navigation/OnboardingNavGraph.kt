package com.twix.onboarding.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.navigation.graphViewModel
import com.twix.navigation_contract.InviteLaunchEventSource
import com.twix.onboarding.OnBoardingViewModel
import com.twix.onboarding.couple.CoupleConnectRoute
import com.twix.onboarding.dday.DdayRoute
import com.twix.onboarding.invite.InviteCodeRoute
import com.twix.onboarding.profile.ProfileRoute
import org.koin.compose.koinInject

object OnboardingNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.OnboardingGraph

    override val startDestination: String
        get() = NavRoutes.CoupleConnectionRoute.route

    override val priority: Int = 2

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.CoupleConnectionRoute.route) { backStackEntry ->
                val vm: OnBoardingViewModel = backStackEntry.graphViewModel(navController, graphRoute.route)
                val inviteLaunchEventSource: InviteLaunchEventSource = koinInject()
                val pendingInviteCode by inviteLaunchEventSource.pendingInviteCode.collectAsStateWithLifecycle()

                LaunchedEffect(pendingInviteCode) {
                    val code = pendingInviteCode ?: return@LaunchedEffect
                    inviteLaunchEventSource.consumePendingInviteCode()
                    navController.navigate(NavRoutes.InviteRoute.createRoute(code))
                }

                CoupleConnectRoute(
                    navigateToNext = {
                        navController.navigate(NavRoutes.InviteRoute.createRoute())
                    },
                    navigateToBack = navController::popBackStack,
                    viewModel = vm,
                )
            }
            composable(
                route = NavRoutes.InviteRoute.route,
                arguments =
                    listOf(
                        navArgument(NavRoutes.InviteRoute.ARG_CODE) {
                            type = NavType.StringType
                            nullable = true
                            defaultValue = null
                        },
                    ),
            ) { backStackEntry ->
                val vm: OnBoardingViewModel = backStackEntry.graphViewModel(navController, graphRoute.route)
                val inviteCode = backStackEntry.arguments?.getString(NavRoutes.InviteRoute.ARG_CODE)

                InviteCodeRoute(
                    navigateToNext = {
                        navController.navigate(NavRoutes.ProfileRoute.route)
                    },
                    navigateToBack = navController::popBackStack,
                    viewModel = vm,
                    initialInviteCode = inviteCode,
                )
            }
            composable(NavRoutes.ProfileRoute.route) { backStackEntry ->
                val vm: OnBoardingViewModel = backStackEntry.graphViewModel(navController, graphRoute.route)

                ProfileRoute(
                    viewModel = vm,
                    navigateToDday = {
                        navController.navigate(NavRoutes.DdayRoute.route)
                    },
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(graphRoute.route) { inclusive = true }
                        }
                    },
                )
            }
            composable(NavRoutes.DdayRoute.route) { backStackEntry ->
                val vm: OnBoardingViewModel = backStackEntry.graphViewModel(navController, graphRoute.route)

                DdayRoute(
                    viewModel = vm,
                    navigateToHome = {
                        navController.navigate(NavRoutes.MainGraph.route) {
                            popUpTo(graphRoute.route) { inclusive = true }
                        }
                    },
                    navigateToBack = {
                        navController.navigate(NavRoutes.ProfileRoute.route)
                    },
                )
            }
        }
    }
}
