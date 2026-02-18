package com.twix.task_certification.detail.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photolog.PhotologDetail
import com.twix.task_certification.detail.model.TaskCertificationDetailUiState

class TaskCertificationDetailPreviewProvider : PreviewParameterProvider<TaskCertificationDetailUiState> {
    override val values =
        sequenceOf(
            TaskCertificationDetailUiState(
                goalId = 1,
                currentShow = BetweenUs.ME,
                myNickname = "나",
                goalName = "아이스크림 먹기",
                partnerNickname = "민정",
                icon = GoalIconType.EXERCISE,
                myPhotolog =
                    PhotologDetail(
                        photologId = 1,
                        goalId = 1,
                        imageUrl = "https://picsum.photos/400/300",
                        verificationDate = "",
                        uploaderName = "나",
                        uploadedAt = "2026-02-11T10:00:00Z",
                        comment = "아이수쿠림",
                        reaction = null,
                    ),
                partnerPhotolog = null,
            ),
            TaskCertificationDetailUiState(
                goalId = 1,
                currentShow = BetweenUs.PARTNER,
                myNickname = "나",
                partnerNickname = "민정",
                icon = GoalIconType.EXERCISE,
                myPhotolog = null,
                partnerPhotolog =
                    PhotologDetail(
                        photologId = 2,
                        goalId = 1,
                        imageUrl = "https://picsum.photos/400/301",
                        verificationDate = "",
                        uploaderName = "민정",
                        uploadedAt = "2026-02-11T09:00:00Z",
                        comment = "인증합니다",
                        reaction = GoalReactionType.FUCK,
                    ),
            ),
        )
}
