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
    onClickSting: () -> Unit,
) {
    Box(Modifier.fillMaxWidth()) {
        BackgroundCard(
            isCertificated = uiState.isDisplayedGoalCertificated,
            uploadedAt = uiState.displayedGoalUpdateAt,
            buttonTitle =
                when (uiState.currentShow) {
                    BetweenUs.ME -> stringResource(R.string.task_certification_take_picture)
                    BetweenUs.PARTNER -> stringResource(R.string.task_certification_detail_partner_sting)
                },
            rotation = if (uiState.currentShow == BetweenUs.ME) -8f else 0f,
            onClick = if (uiState.currentShow == BetweenUs.ME) onClickUpload else onClickSting,
        )

        SwipeableCard(
            onSwipe = onSwipe,
            modifier = Modifier.fillMaxWidth(),
        ) {
            ForegroundCard(
                isCertificated = uiState.isDisplayedGoalCertificated,
                nickName = uiState.displayedNickname,
                imageUrl = uiState.displayedGoalImageUrl,
                comment = uiState.displayedGoalComment,
                currentShow = uiState.currentShow,
                rotation = if (uiState.currentShow == BetweenUs.ME) 0f else -8f,
            )
        }
    }
}
