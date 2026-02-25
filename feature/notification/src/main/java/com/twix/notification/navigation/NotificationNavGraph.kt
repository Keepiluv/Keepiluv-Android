package com.twix.notification.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.twix.domain.model.enums.BetweenUs
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.notification.NotificationRoute

object NotificationNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.NotificationGraph
    override val startDestination: String
        get() = NavRoutes.NotificationRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        navigation(
            route = graphRoute.route,
            startDestination = startDestination,
        ) {
            composable(NavRoutes.NotificationRoute.route) {
                NotificationRoute(
                    popBackStack = { navController.popBackStack() },
                    navigateToMyPhotolog = { goalId, date ->
                        navController.navigate(NavRoutes.TaskCertificationDetailRoute.createRoute(goalId, date, BetweenUs.ME.name)) {
                            launchSingleTop = true
                        }
                    },
                    navigateToPartnerPhotolog = { goalId, date ->
                        navController.navigate(
                            NavRoutes.TaskCertificationDetailRoute.createRoute(
                                goalId,
                                date,
                                BetweenUs.PARTNER.name,
                            ),
                        ) {
                            launchSingleTop = true
                        }
                    },
                    navigateToStatisticsEndedGoals = {
                        // TODO: 통계 화면으로 이동하는 로직
                        navController.popBackStack()
                    },
                )
            }
        }
    }
}
