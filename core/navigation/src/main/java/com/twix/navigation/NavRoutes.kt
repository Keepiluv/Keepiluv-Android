package com.twix.navigation

import android.net.Uri
import com.twix.navigation.args.DetailNavArgs
import kotlinx.serialization.json.Json
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
     * PhotologGraph
     * */
    object PhotologGraph : NavRoutes("photolog_graph")

    object PhotologDetailRoute :
        NavRoutes("photolog_detail/{goalId}/{date}/{betweenUs}?isCompleted={isCompleted}") {
        const val ARG_GOAL_ID = "goalId"
        const val ARG_DATE = "date"
        const val ARG_BETWEEN_US = "betweenUs"
        const val ARG_IS_COMPLETED = "isCompleted"

        fun createRoute(
            goalId: Long,
            date: LocalDate,
            betweenUs: String,
            isCompleted: Boolean = false,
        ) = "photolog_detail/$goalId/$date/$betweenUs?isCompleted=$isCompleted"
    }

    object PhotologRoute : NavRoutes("photolog/{data}") {
        const val ARG_DATA = "data"

        enum class From {
            HOME,
            DETAIL,
            EDITOR,
        }

        fun createRoute(data: DetailNavArgs): String {
            val json = Json.encodeToString(data)
            val encoded = Uri.encode(json)
            return "photolog/$encoded"
        }
    }

    object PhotologEditorRoute :
        NavRoutes("photolog_editor/{goalId}/{date}") {
        const val ARG_GOAL_ID = "goalId"
        const val ARG_DATE = "date"

        fun createRoute(
            goalId: Long,
            date: LocalDate,
        ) = "photolog_editor/$goalId/$date"
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

    object GoalEditorRoute : NavRoutes("goal_editor/{id}?presetId={presetId}") {
        const val ARG_ID = "id"
        const val ARG_PRESET_ID = "presetId"

        fun createRoute(
            id: Long,
            presetId: String? = null,
        ): String {
            val baseRoute = "goal_editor/$id"
            return presetId?.let { "$baseRoute?presetId=${Uri.encode(it)}" } ?: baseRoute
        }
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

    /**
     * StatsGraph
     * */
    object StatsDetailGraph : NavRoutes("stats_detail_graph")

    object StatsDetailRoute : NavRoutes("stats_detail_graph/{goalId}/{date}") {
        const val ARG_GOAL_ID = "goalId"
        const val ARG_DATE = "date"

        fun createRoute(
            goalId: Long,
            date: LocalDate,
        ): String = "stats_detail_graph/$goalId/$date"
    }

    /**
     * NotificationGraph
     * */
    object NotificationGraph : NavRoutes("notification_graph")

    object NotificationRoute : NavRoutes("notification")

    /**
     * SplashGraph
     * */
    object SplashGraph : NavRoutes("splash_graph")

    object SplashRoute : NavRoutes("splash")
}
