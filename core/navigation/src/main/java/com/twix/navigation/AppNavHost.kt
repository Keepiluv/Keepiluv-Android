package com.twix.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.twix.domain.model.enums.BetweenUs
import com.twix.navigation.base.NavGraphContributor
import com.twix.navigation_contract.AppNavigator
import com.twix.navigation_contract.NotificationDeepLinkHandler
import com.twix.navigation_contract.NotificationLaunchEventSource
import org.koin.compose.getKoin
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun AppNavHost(
    notificationLaunchEventSource: NotificationLaunchEventSource,
    notificationRouter: NotificationDeepLinkHandler = koinInject(),
) {
    val navController = rememberNavController()
    val koin = getKoin()
    val contributors =
        remember {
            koin.getAll<NavGraphContributor>().sortedBy { it.priority }
        }
    val start =
        contributors
            .firstOrNull { it.graphRoute == NavRoutes.LoginGraph }
            ?.graphRoute
            ?: error("해당 Graph를 찾을 수 없습니다.")
    val pendingDeepLink by notificationLaunchEventSource.pendingDeepLink.collectAsStateWithLifecycle()
    val appNavigator =
        remember(navController) {
            object : AppNavigator {
                override fun toHome() {
                    navController.navigate(NavRoutes.MainGraph) { launchSingleTop = true }
                }

                override fun toLogin() {
                    navController.navigate(NavRoutes.LoginGraph) {
                        launchSingleTop = true
                        popUpTo(NavRoutes.LoginGraph) {
                            inclusive = true
                        }
                    }
                }

                override fun toMyPhotolog(
                    goalId: Long,
                    date: LocalDate,
                ) {
                    ensureMainStack(navController)
                    navController.navigate(
                        NavRoutes.TaskCertificationDetailRoute.createRoute(
                            goalId = goalId,
                            date = date,
                            betweenUs = BetweenUs.ME.name,
                        ),
                    ) {
                        launchSingleTop = true
                    }
                }

                override fun toPartnerPhotolog(
                    goalId: Long,
                    date: LocalDate,
                ) {
                    ensureMainStack(navController)
                    navController.navigate(
                        NavRoutes.TaskCertificationDetailRoute.createRoute(
                            goalId = goalId,
                            date = date,
                            betweenUs = BetweenUs.PARTNER.name,
                        ),
                    ) {
                        launchSingleTop = true
                    }
                }

                override fun toStatisticsEndedGoals() {
                    ensureMainStack(navController)
                    TODO("Not yet implemented")
                }
            }
        }
    val duration = 300

    LaunchedEffect(pendingDeepLink) {
        val deepLink = pendingDeepLink ?: return@LaunchedEffect

        try {
            notificationRouter.handle(
                rawDeepLink = deepLink,
                navigator = appNavigator,
            )
        } finally {
            notificationLaunchEventSource.consumePendingDeepLink(deepLink)
        }
    }

    NavHost(
        navController = navController,
        startDestination = start.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(duration, easing = FastOutSlowInEasing),
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(duration, easing = FastOutSlowInEasing),
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(duration, easing = FastOutSlowInEasing),
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(duration, easing = FastOutSlowInEasing),
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) {
        contributors.forEach { with(it) { registerGraph(navController) } }
    }
}

/**
 * 푸쉬알림 클릭으로 앱 진입 시 네비게이션 백스택이 없어서 뒤로가기를 누르면 바로 앱이 종료될 수 있음
 * 이를 방지하기 위해 백스택에 MainGraph를 미리 넣어두는 메서드
 * hierarchy는 현재 화면이 속하는 그래프를 검사할 수 있게 해줌
 * */
private fun ensureMainStack(navController: NavHostController) {
    val inMainGraph =
        navController.currentDestination
            ?.hierarchy
            ?.any { it.route == NavRoutes.MainGraph.route } == true

    if (!inMainGraph) {
        navController.navigate(NavRoutes.MainGraph) {
            launchSingleTop = true
        }
    }
}
