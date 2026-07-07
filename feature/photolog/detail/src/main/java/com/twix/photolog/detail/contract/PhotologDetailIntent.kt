package com.twix.photolog.detail.contract

import com.twix.domain.model.enums.GoalReactionType
import com.twix.ui.base.Intent

sealed interface PhotologDetailIntent : Intent {
    data object Retry : PhotologDetailIntent

    data object Refresh : PhotologDetailIntent

    data class Reaction(
        val type: GoalReactionType,
    ) : PhotologDetailIntent

    data object Poke : PhotologDetailIntent

    data object SwipeCard : PhotologDetailIntent

    data object MyReactionEffected : PhotologDetailIntent
}
