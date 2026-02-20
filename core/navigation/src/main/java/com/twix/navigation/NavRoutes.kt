package com.twix.navigation

import java.time.LocalDate

/**
 * 앱 전반에서 사용하는 Navigation Route를 여기에서 정의합니다.
 *
 * · 문자열 route를 직접 사용하지 않고 타입 안정성을 위해 sealed class를 활용합니다.
 * · navigation argument가 필요한 경우 createRoute()를 통해 route를 생성합니다.
 * · Graph의 경우 _graph로 네이밍하고 Screen의 경우에는 Composable명에서 Screen을 제외한 앞부분을 활용합니다. ex) HomeScreen -> home
 * */
sealed class NavRoutes(
    val route: String,
) {
    /**
     * LoginGraph
     * */
    object LoginGraph : NavRoutes("login_graph")

    object LoginRoute : NavRoutes("login")

    /**
     * MainGraph
     * */
    object MainGraph : NavRoutes("main_graph")

    object MainRoute : NavRoutes("main")

    /**
     * TaskCertificationGraph
     * */
    object TaskCertificationGraph : NavRoutes("task_certification_graph")

    object TaskCertificationDetailRoute :
        NavRoutes("task_certification_detail/{goalId}/{date}/{betweenUs}") {
        const val ARG_GOAL_ID = "goalId"
        const val ARG_DATE = "date"
        const val ARG_BETWEEN_US = "betweenUs"

        fun createRoute(
            goalId: Long,
            date: LocalDate,
            betweenUs: String,
        ) = "task_certification_detail/$goalId/$date/$betweenUs"
    }

    object TaskCertificationRoute : NavRoutes("task_certification/{goalId}/{from}") {
        const val ARG_GOAL_ID = "goalId"
        const val ARG_FROM = "from"

        enum class From {
            HOME,
            DETAIL,
        }

        fun createRoute(
            goalId: Long,
            from: From,
        ) = "task_certification/$goalId/${from.name}"
    }

    /**
     * OnboardingGraph
     * */
    object OnboardingGraph : NavRoutes("onboarding_graph")

    object OnboardingRoute : NavRoutes("onboarding")

    object CoupleConnectionRoute : NavRoutes("couple_connect")

    object InviteRoute : NavRoutes("invite")

    object ProfileRoute : NavRoutes("profile")

    object DdayRoute : NavRoutes("dday")

    /**
     * GoalEditorGraph
     * */
    object GoalEditorGraph : NavRoutes("goal_editor_graph")

    object GoalEditorRoute : NavRoutes("goal_editor/{id}") {
        const val ARG_ID = "id"

        fun createRoute(id: Long) = "goal_editor/$id"
    }

    /**
     * GoalManageGraph
     * */
    object GoalManageGraph : NavRoutes("goal_manage_graph")

    object GoalManageRoute : NavRoutes("goal_manage/{date}") {
        const val ARG_DATE = "date"

        fun createRoute(date: LocalDate) = "goal_manage/$date"
    }

    /**
     * SettingsGraph
     * */
    object SettingsGraph : NavRoutes("settings_graph")

    object SettingsRoute : NavRoutes("settings")

    object SettingsAccountRoute : NavRoutes("settings/account")

    object SettingsAboutRoute : NavRoutes("settings/about")
}
