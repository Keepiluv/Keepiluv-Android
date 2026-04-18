package com.twix.main.model

import com.twix.designsystem.R

enum class MainTab(
    val selectedIcon: Int,
    val unselectedIcon: Int,
    val title: Int,
) {
    HOME(
        selectedIcon = R.drawable.ic_home_selected,
        unselectedIcon = R.drawable.ic_home_unselected,
        title = R.string.word_home,
    ),
    STATS(
        selectedIcon = R.drawable.ic_stats_selected,
        unselectedIcon = R.drawable.ic_stats_unselected,
        title = R.string.word_stats,
    ),
    COUPLE(
        selectedIcon = R.drawable.ic_couple_selected,
        unselectedIcon = R.drawable.ic_couple_unselected,
        title = R.string.word_couple_page,
    ),
}
