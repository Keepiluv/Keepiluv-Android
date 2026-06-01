package com.twix.settings.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.navigation.NavRoutes
import com.twix.navigation.base.NavGraphContributor
import com.twix.settings.SettingsRoute
import com.twix.settings.SettingsSideEffect
import com.twix.settings.SettingsViewModel
import com.twix.settings.about.SettingsAboutRoute
import com.twix.settings.account.SettingsAccountRoute
import com.twix.settings.notification.SettingsNotificationRoute
import com.twix.ui.base.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

object SettingsNavGraph : NavGraphContributor {
    override val graphRoute: NavRoutes
        get() = NavRoutes.SettingsGraph
    override val startDestination: String
        get() = NavRoutes.SettingsRoute.route

    override fun NavGraphBuilder.registerGraph(navController: NavHostController) {
        composable(graphRoute.route) {
            SettingsGraphHost(rootNavController = navController)
        }
    }
}

@Composable
private fun SettingsGraphHost(
    rootNavController: NavHostController,
    viewModel: SettingsViewModel = koinViewModel(),
    toastManager: ToastManager = koinInject(),
) {
    val settingsNavController = rememberNavController()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    ObserveAsEvents(viewModel.sideEffect) { effect ->
        when (effect) {
            SettingsSideEffect.NavigateToLogin ->
                rootNavController.navigate(NavRoutes.LoginRoute.route) {
                    launchSingleTop = true
                    popUpTo(NavRoutes.SettingsGraph.route) {
                        inclusive = true
                    }
                }
            is SettingsSideEffect.ShowToast ->
                toastManager.show(ToastData(currentContext.getString(effect.resId), effect.type))
        }
    }

    NavHost(
        navController = settingsNavController,
        startDestination = NavRoutes.SettingsRoute.route,
        enterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            )
        },
        exitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            )
        },
        popEnterTransition = {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth },
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            )
        },
        popExitTransition = {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(NAVIGATION_ANIMATION_DURATION, easing = FastOutSlowInEasing),
            )
        },
    ) {
        composable(NavRoutes.SettingsRoute.route) {
            SettingsRoute(
                viewModel = viewModel,
                popBackStack = { rootNavController.popBackStack() },
                navigateToSettingsAccount = {
                    settingsNavController.navigate(NavRoutes.SettingsAccountRoute.route) {
                        launchSingleTop = true
                    }
                },
                navigateToSettingsAbout = {
                    settingsNavController.navigate(NavRoutes.SettingsAboutRoute.route) {
                        launchSingleTop = true
                    }
                },
                navigateToSettingsNotification = {
                    settingsNavController.navigate(NavRoutes.SettingsNotificationRoute.route) {
                        launchSingleTop = true
                    }
                },
            )
        }

        composable(NavRoutes.SettingsAccountRoute.route) {
            SettingsAccountRoute(
                viewModel = viewModel,
                popBackStack = { settingsNavController.popBackStack() },
            )
        }

        composable(NavRoutes.SettingsAboutRoute.route) {
            SettingsAboutRoute(
                popBackStack = { settingsNavController.popBackStack() },
            )
        }

        composable(NavRoutes.SettingsNotificationRoute.route) {
            SettingsNotificationRoute(
                viewModel = viewModel,
                popBackStack = { settingsNavController.popBackStack() },
            )
        }
    }
}

private const val NAVIGATION_ANIMATION_DURATION = 300
