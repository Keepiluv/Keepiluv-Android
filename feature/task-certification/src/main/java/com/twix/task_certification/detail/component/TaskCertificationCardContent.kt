package com.twix.task_certification.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.photolog.BackgroundCard
import com.twix.designsystem.components.photolog.ForegroundCard
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.BetweenUs
import com.twix.task_certification.detail.component.reaction.ReactionUiModel
import com.twix.task_certification.detail.component.swipe.SwipeableCard
import com.twix.task_certification.detail.contract.TaskCertificationDetailUiState
import com.twix.task_certification.detail.preview.TaskCertificationDetailPreviewProvider

@Composable
internal fun TaskCertificationCardContent(
    uiState: TaskCertificationDetailUiState,
    onSwipe: () -> Unit,
    onClickUpload: () -> Unit,
    onPoke: () -> Unit,
) {
    Box {
        BackgroundCard(
            uploadedAt = uiState.displayedGoalUpdateAt,
            actionLabel =
                when (uiState.currentShow) {
                    BetweenUs.ME -> stringResource(R.string.task_certification_take_picture)
                    BetweenUs.PARTNER -> stringResource(R.string.action_poke)
                },
            rotation = if (uiState.isDisplayedMyPhotolog) -8f else 0f,
            onClickAction = if (uiState.isDisplayedMyPhotolog) onClickUpload else onPoke,
            showActionButton = uiState.showActionButton,
        )

        SwipeableCard(
            onSwipe = onSwipe,
            isDisplayingMyPhoto = uiState.isDisplayedMyPhotolog,
        ) {
            ForegroundCard(
                isCertificated = uiState.isDisplayedGoalCertificated,
                nickName = uiState.displayedNickname,
                imageUrl = uiState.displayedGoalImageUrl,
                comment = uiState.displayedGoalComment,
                currentShow = uiState.currentShow,
                rotation = if (uiState.isDisplayedMyPhotolog) 0f else -8f,
            )
        }

        MyReactionBadge(
            visible = uiState.showMyPhotologReaction,
            reaction = uiState.myReaction,
            modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = (-8).dp, y = (-13).dp),
        )
    }
}

@Composable
private fun MyReactionBadge(
    visible: Boolean,
    reaction: ReactionUiModel?,
    modifier: Modifier = Modifier,
) {
    if (!visible) return

    reaction?.let {
        Box(modifier = modifier) {
            Image(
                painter = painterResource(R.drawable.ic_my_reaction_union),
                contentDescription = null,
            )

            Image(
                painter = painterResource(it.imageResources),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(bottom = 10.dp)
                        .align(Alignment.Center),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskCertificationCardContentPreview(
    @PreviewParameter(TaskCertificationDetailPreviewProvider::class)
    uiState: TaskCertificationDetailUiState,
) {
    TwixTheme {
        TaskCertificationCardContent(
            uiState = uiState.copy(isLoading = true),
            onSwipe = {},
            onClickUpload = {},
            onPoke = {},
        )
    }
}
