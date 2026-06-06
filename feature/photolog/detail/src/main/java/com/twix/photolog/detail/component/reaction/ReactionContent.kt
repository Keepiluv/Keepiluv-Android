package com.twix.photolog.detail.component.reaction

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.GoalReactionType

@Composable
internal fun ReactionContent(
    screenHeightPx: Float,
    modifier: Modifier = Modifier,
    reaction: GoalReactionType? = null,
    onClickReaction: (GoalReactionType) -> Unit,
) {
    var effectTarget by remember { mutableStateOf<ReactionUiModel?>(null) }

    Box(
        modifier =
            modifier
                .fillMaxSize(),
    ) {
        ReactionBar(
            selectedReaction = reaction,
            onSelectReaction = { type ->
                onClickReaction(type)
                effectTarget = ReactionUiModel.find(type)
            },
            modifier =
                Modifier
                    .padding(horizontal = 20.dp),
        )

        ReactionEffect(
            targetReaction = effectTarget,
            spec = ReactionEffectSpec(travelDistanceRange = 500..screenHeightPx.toInt()),
        )

        Spacer(modifier = Modifier.padding(bottom = 100.dp))
    }
}

@Preview
@Composable
private fun ReactionContentPreview() {
    TwixTheme {
        ReactionContent(
            screenHeightPx = 0f,
            onClickReaction = {},
        )
    }
}
