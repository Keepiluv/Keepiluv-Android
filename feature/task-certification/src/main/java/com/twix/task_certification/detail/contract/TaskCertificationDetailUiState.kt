package com.twix.task_certification.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.model.photolog.PhotologDetail
import com.twix.ui.base.State
import com.twix.util.RelativeTimeFormatter
import java.time.LocalDate

@Immutable
data class TaskCertificationDetailUiState(
    val goalId: Long = -1L,
    val currentShow: BetweenUs = BetweenUs.PARTNER,
    val selectedDate: LocalDate = LocalDate.now(),
    val myNickname: String = "",
    val partnerNickname: String = "",
    val goalName: String = "",
    val icon: GoalIconType = GoalIconType.DEFAULT,
    val myPhotolog: PhotologDetail? = null,
    val partnerPhotolog: PhotologDetail? = null,
    val isCompletedGoal: Boolean = false,
    /**
     * 초기값으로 인해 찌르기/업로드 버튼이 렌더링 되는 것을 막기 위한 변수
     * */
    val isLoading: Boolean = false,
) : State {
    val isDisplayedGoalCertificated: Boolean
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog != null
                BetweenUs.PARTNER -> partnerPhotolog != null
            }

    val displayedGoalUpdateAt: String
        get() =
            when (currentShow) {
                BetweenUs.ME ->
                    myPhotolog?.uploadedAt?.let {
                        RelativeTimeFormatter.format(it)
                    } ?: ""

                BetweenUs.PARTNER ->
                    partnerPhotolog?.uploadedAt?.let {
                        RelativeTimeFormatter.format(
                            it,
                        )
                    } ?: ""
            }

    val displayedGoalImageUrl: String?
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog?.imageUrl
                BetweenUs.PARTNER -> partnerPhotolog?.imageUrl
            }

    val displayedGoalComment: String?
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog?.comment
                BetweenUs.PARTNER -> partnerPhotolog?.comment
            }

    val displayedNickname: String
        get() =
            when (currentShow) {
                BetweenUs.ME -> myNickname
                BetweenUs.PARTNER -> partnerNickname
            }

    val isDisplayedMyPhotolog: Boolean
        get() =
            currentShow == BetweenUs.ME

    val canModify: Boolean
        get() =
            currentShow == BetweenUs.ME && isDisplayedGoalCertificated

    val canReaction: Boolean
        get() =
            currentShow == BetweenUs.PARTNER && isDisplayedGoalCertificated

    val showActionButton: Boolean
        get() = !isCompletedGoal && !isDisplayedGoalCertificated
}

fun PhotoLogs.toUiState(
    goalId: Long,
    betweenUs: String,
    selectedDate: LocalDate,
    isCompletedGoal: Boolean,
): TaskCertificationDetailUiState {
    val currentGoalPhotolog =
        goals.firstOrNull {
            it.goalId == goalId
        } ?: return TaskCertificationDetailUiState()

    return TaskCertificationDetailUiState(
        goalId = goalId,
        currentShow = BetweenUs.valueOf(betweenUs),
        selectedDate = selectedDate,
        myNickname = myNickname,
        partnerNickname = partnerNickname,
        goalName = currentGoalPhotolog.goalName,
        icon = currentGoalPhotolog.icon,
        myPhotolog = currentGoalPhotolog.myPhotolog,
        partnerPhotolog = currentGoalPhotolog.partnerPhotolog,
        isCompletedGoal = isCompletedGoal,
    )
}
