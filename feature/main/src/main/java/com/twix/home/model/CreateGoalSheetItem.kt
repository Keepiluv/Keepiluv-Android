package com.twix.home.model

import com.twix.domain.model.enums.GoalIconType

sealed interface CreateGoalSheetItem {
    data object DirectAdd : CreateGoalSheetItem

    data class Preset(
        val presetId: String,
        val title: String,
        val icon: GoalIconType,
    ) : CreateGoalSheetItem
}
