package com.twix.task_certification.detail.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
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
import com.twix.task_certification.detail.reaction.ReactionBar
import com.twix.task_certification.detail.reaction.ReactionEffect
import com.twix.task_certification.detail.reaction.ReactionUiModel

@Composable
internal fun ReactionContent(
    reaction: GoalReactionType? = null,
    onClickReaction: (GoalReactionType) -> Unit,
) {
    var effectTarget by remember { mutableStateOf<ReactionUiModel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            verticalArrangement = Arrangement.Bottom,
        ) {
            Spacer(Modifier.height(85.dp))
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
        }

        ReactionEffect(
            targetReaction = effectTarget,
            modifier = Modifier.padding(bottom = 100.dp),
        )
    }
}

@Preview
@Composable
private fun ReactionContentPreview() {
    TwixTheme {
        ReactionContent(
            onClickReaction = {},
        )
    }
}
