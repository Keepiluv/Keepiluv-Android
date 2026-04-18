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
    /**
     * 현재 [currentShow]에 해당하는 사용자의 인증샷 인증 여부
     *
     * - [BetweenUs.ME]: 내 인증샷이 존재하면 `true`
     * - [BetweenUs.PARTNER]: 파트너 인증샷이 존재하면 `true`
     */
    val isDisplayedGoalCertificated: Boolean
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog != null
                BetweenUs.PARTNER -> partnerPhotolog != null
            }

    /**
     * 현재 [currentShow]에 해당하는 인증샷의 업로드 시간을 상대적 시간 문자열
     *
     * - [BetweenUs.ME]: 내 인증샷 업로드 시간
     * - [BetweenUs.PARTNER]: 파트너 인증샷 업로드 시간
     */
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

    /**
     * 현재 [currentShow]에 해당하는 인증샷 URL
     *
     * - [BetweenUs.ME]: 내 인증샷 이미지 URL
     * - [BetweenUs.PARTNER]: 파트너 인증샷 이미지 URL
     *
     */
    val displayedGoalImageUrl: String?
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog?.imageUrl
                BetweenUs.PARTNER -> partnerPhotolog?.imageUrl
            }

    /**
     * 현재 [currentShow]에 해당하는 인증샷의 코멘트
     *
     * - [BetweenUs.ME]: 내 코멘트
     * - [BetweenUs.PARTNER]: 파트너 코멘트
     *
     */
    val displayedGoalComment: String?
        get() =
            when (currentShow) {
                BetweenUs.ME -> myPhotolog?.comment
                BetweenUs.PARTNER -> partnerPhotolog?.comment
            }

    /**
     * 현재 [currentShow]에 해당하는 사용자의 닉네임
     *
     * - [BetweenUs.ME]: 내 닉네임
     * - [BetweenUs.PARTNER]: 파트너 닉네임
     */
    val displayedNickname: String
        get() =
            when (currentShow) {
                BetweenUs.ME -> myNickname
                BetweenUs.PARTNER -> partnerNickname
            }

    /**
     * 현재 화면이 내 인증샷을 표시하는 상태인지 여부
     *
     * 내 인증샷일 때 `true`
     */
    val isDisplayedMyPhotolog: Boolean
        get() = currentShow == BetweenUs.ME

    /**
     * 내 인증샷를 수정할 수 있는지 여부 반환
     *
     * 현재 내 인증샷를 보고 있고([isDisplayedMyPhotolog]),
     * 인증이 완료된 상태([isDisplayedGoalCertificated])일 때 `true`
     */
    val canModify: Boolean
        get() = currentShow == BetweenUs.ME && isDisplayedGoalCertificated

    /**
     * 파트너 인증샷에 리액션을 남길 수 있는지 여부
     *
     * 현재 파트너 인증샷을 보고 있고([BetweenUs.PARTNER]),
     * 파트너의 인증이 완료된 상태([isDisplayedGoalCertificated])일 때 `true`
     */
    val canReaction: Boolean
        get() = currentShow == BetweenUs.PARTNER && isDisplayedGoalCertificated

    /**
     * 인증샷 업로드 또는 찌르기 액션 버튼을 표시할지 여부를 반환
     *
     * 목표가 완료되지 않았고([isCompletedGoal]이 `false`),
     * 현재 대상의 인증이 없는 상태([isDisplayedGoalCertificated]가 `false`)일 때 `true`
     */
    val showActionButton: Boolean
        get() = !isCompletedGoal && !isDisplayedGoalCertificated

    /**
     * 내 인증샷에 달린 리액션 뱃지를 표시할지 여부를 반환
     *
     * 내 인증샷을 보고 있고([isDisplayedMyPhotolog]),
     * 리액션이 존재하며([myPhotolog]의 reaction이 non-null) 일 때 `true`
     */
    val showMyPhotologReactionBadge: Boolean
        get() =
            isDisplayedMyPhotolog &&
                myPhotolog?.reaction != null

    /**
     * 내 인증샷의 리액션을 [ReactionUiModel]로 변환하여 반환
     */
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
