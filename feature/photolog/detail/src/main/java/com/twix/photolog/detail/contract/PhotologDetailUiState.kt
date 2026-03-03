package com.twix.photolog.detail.contract

import androidx.compose.runtime.Immutable
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.model.photolog.PhotologDetail
import com.twix.photolog.detail.component.reaction.ReactionUiModel
import com.twix.ui.base.State
import com.twix.util.RelativeTimeFormatter
import java.time.LocalDate

@Immutable
data class PhotologDetailUiState(
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
     * 내 인증샷에 상대방이 리액션을 남겼을 경우 최초 1회 인터렉션 렌더링을 위한 변수
     */
    val hasShownMyReaction: Boolean = false,
    /**
     * 초기값으로 인해 찌르기/업로드 버튼이 렌더링 되는 것을 막기 위한 변수
     */
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
                        RelativeTimeFormatter.format(it)
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
        get() = currentShow == BetweenUs.ME

    val canModify: Boolean
        get() = currentShow == BetweenUs.ME && isDisplayedGoalCertificated

    val canReaction: Boolean
        get() = currentShow == BetweenUs.PARTNER && isDisplayedGoalCertificated

    val showActionButton: Boolean
        get() = !isCompletedGoal && !isDisplayedGoalCertificated

    val showMyPhotologReactionBadge: Boolean
        get() =
            isDisplayedMyPhotolog &&
                myPhotolog?.reaction != null

    val myReaction: ReactionUiModel?
        get() = myPhotolog?.reaction?.let { ReactionUiModel.find(it) }
}

fun PhotoLogs.toUiState(
    goalId: Long,
    betweenUs: String,
    selectedDate: LocalDate,
    isCompletedGoal: Boolean,
): PhotologDetailUiState {
    val currentGoalPhotolog =
        goals.firstOrNull {
            it.goalId == goalId
        } ?: return PhotologDetailUiState()

    return PhotologDetailUiState(
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
