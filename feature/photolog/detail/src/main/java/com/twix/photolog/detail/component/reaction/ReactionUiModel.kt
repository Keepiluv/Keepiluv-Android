package com.twix.photolog.detail.component.reaction

import com.twix.designsystem.R
import com.twix.domain.model.enums.GoalReactionType

enum class ReactionUiModel(
    val type: GoalReactionType,
    val imageResources: Int,
) {
    HAPPY(GoalReactionType.HAPPY, R.drawable.ic_emoji_happy),
    TROUBLE(GoalReactionType.TROUBLE, R.drawable.ic_emoji_trouble),
    LOVE(GoalReactionType.LOVE, R.drawable.ic_emoji_love),
    DOUBT(GoalReactionType.DOUBT, R.drawable.ic_emoji_doubt),
    FUCK(GoalReactionType.FUCK, R.drawable.ic_emoji_fuck),
    ;

    companion object {
        fun find(type: GoalReactionType) =
            entries.find { it.type == type }
                ?: throw IllegalArgumentException(UNKNOWN_REACTION_TYPE_MESSAGE.format(type))

        private const val UNKNOWN_REACTION_TYPE_MESSAGE = "Unknown GoalReactionType: %s"
    }
}
