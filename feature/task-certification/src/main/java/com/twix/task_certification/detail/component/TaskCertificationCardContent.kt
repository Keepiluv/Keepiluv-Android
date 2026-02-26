package com.twix.task_certification.detail.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.twix.designsystem.R
import com.twix.designsystem.components.photolog.BackgroundCard
import com.twix.designsystem.components.photolog.ForegroundCard
import com.twix.domain.model.enums.BetweenUs
import com.twix.task_certification.detail.component.swipe.SwipeableCard
import com.twix.task_certification.detail.contract.TaskCertificationDetailUiState

@Composable
internal fun TaskCertificationCardContent(
    uiState: TaskCertificationDetailUiState,
    onSwipe: () -> Unit,
    onClickUpload: () -> Unit,
    onPoke: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
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
            modifier = Modifier.fillMaxWidth(),
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
    }
}
