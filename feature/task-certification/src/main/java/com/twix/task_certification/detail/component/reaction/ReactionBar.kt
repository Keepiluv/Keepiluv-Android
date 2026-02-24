package com.twix.task_certification.detail.component.reaction

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.GoalReactionType
import com.twix.ui.extension.noRippleClickable

@Composable
fun ReactionBar(
    onSelectReaction: (GoalReactionType) -> Unit,
    modifier: Modifier = Modifier,
    selectedReaction: GoalReactionType? = null,
) {
    val shape = RoundedCornerShape(999.dp)
    val height = 68.dp
    val shadowOffset = 10.dp
    val shadowStart = 13.dp

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(height + shadowOffset),
    ) {
        Box(
            modifier =
                Modifier
                    .matchParentSize()
                    .padding(start = shadowStart, top = shadowOffset)
                    .background(GrayColor.C200, shape),
        )

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(height)
                    .border(width = 1.dp, color = GrayColor.C500, shape = shape)
                    .background(GrayColor.C100, shape)
                    .clip(shape),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ReactionUiModel.entries.forEachIndexed { index, reaction ->
                val isSelected = reaction.type == selectedReaction

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (isSelected) GrayColor.C300 else GrayColor.C100,
                            ).noRippleClickable(onClick = { onSelectReaction(reaction.type) }),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        imageVector = ImageVector.vectorResource(reaction.imageResources),
                        contentDescription = null,
                    )
                }

                if (index < 4) {
                    VerticalDivider(
                        modifier = Modifier.fillMaxHeight(),
                        color = GrayColor.C500,
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ReactionBarPreview() {
    TwixTheme {
        ReactionBar(
            selectedReaction = GoalReactionType.FUCK,
            onSelectReaction = { },
        )
    }
}
