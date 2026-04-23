package com.twix.photolog.detail.component

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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.twix.designsystem.R
import com.twix.designsystem.components.photolog.BackgroundCard
import com.twix.designsystem.components.photolog.ForegroundCard
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.BetweenUs
import com.twix.photolog.detail.component.reaction.ReactionUiModel
import com.twix.photolog.detail.component.swipe.SwipeableCard
import com.twix.photolog.detail.contract.PhotologDetailUiState
import com.twix.photolog.detail.preview.PhotologDetailPreviewProvider
import kotlin.math.roundToInt

@Composable
internal fun PhotologCardContent(
    uiState: PhotologDetailUiState,
    isPokeDisabled: Boolean,
    onSwipe: () -> Unit,
    onClickUpload: () -> Unit,
    onPoke: () -> Unit,
) {
    SwipeableCard(
        isShowMyCard = uiState.isDisplayedMyPhotolog,
        onSwipe = onSwipe,
    ) { swipeState ->
        val effectiveIsFrontMyCard =
            if (swipeState.isCrossingDuringDrag) {
                !uiState.isDisplayedMyPhotolog
            } else {
                uiState.isDisplayedMyPhotolog
            }

        Box {
            // 뒷 카드
            if (swipeState.cardOffset == 0f) {
                BackgroundCard(
                    uploadedAt =
                        uiState.displayedGoalUploadedAt?.let { certifiedAt ->
                            formatCertificationTime(CertificationTime.from(certifiedAt))
                        } ?: "",
                    actionLabel =
                        when (uiState.currentShow) {
                            BetweenUs.ME -> stringResource(R.string.photolog_picture_upload)
                            BetweenUs.PARTNER -> stringResource(R.string.action_poke)
                        },
                    rotation = if (uiState.isDisplayedMyPhotolog) -8f else 0f,
                    onClickAction =
                        if (uiState.isDisplayedMyPhotolog) {
                            onClickUpload
                        } else {
                            { if (!isPokeDisabled) onPoke() }
                        },
                    showActionButton = uiState.showActionButton,
                )
            }

            // 내 카드
            Box(
                modifier =
                    Modifier
                        .zIndex(if (effectiveIsFrontMyCard) 1f else 0f)
                        .offset {
                            IntOffset(
                                (swipeState.cardOffset * (if (effectiveIsFrontMyCard) 1f else -1f)).roundToInt(),
                                0,
                            )
                        },
            ) {
                ForegroundCard(
                    isCertificated = uiState.myPhotolog != null,
                    nickName = uiState.myNickname,
                    imageUrl = uiState.myPhotolog?.imageUrl,
                    comment = uiState.myPhotolog?.comment ?: "",
                    currentShow = BetweenUs.ME,
                    rotation = if (uiState.isDisplayedMyPhotolog) 0f else -8f,
                )
            }

            // 상대방 카드
            Box(
                modifier =
                    Modifier
                        .zIndex(if (effectiveIsFrontMyCard) 0f else 1f)
                        .offset {
                            IntOffset(
                                (swipeState.cardOffset * (if (effectiveIsFrontMyCard) -1f else 1f)).roundToInt(),
                                0,
                            )
                        },
            ) {
                ForegroundCard(
                    isCertificated = uiState.partnerPhotolog != null,
                    nickName = uiState.partnerNickname,
                    imageUrl = uiState.partnerPhotolog?.imageUrl,
                    comment = uiState.partnerPhotolog?.comment ?: "",
                    currentShow = BetweenUs.PARTNER,
                    rotation = if (uiState.isDisplayedMyPhotolog) -8f else 0f,
                )
            }

            MyReactionBadge(
                visible = uiState.showMyPhotologReactionBadge,
                reaction = uiState.myReaction,
                modifier =
                    Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = (-8).dp, y = (-13).dp),
            )
        }
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

@Composable
private fun formatCertificationTime(certificationTime: CertificationTime?): String =
    when (certificationTime) {
        is CertificationTime.JustNow -> stringResource(R.string.certification_time_just_now)
        is CertificationTime.Minutes -> stringResource(R.string.certification_time_minutes_ago, certificationTime.value)
        is CertificationTime.Hours -> stringResource(R.string.certification_time_hours_ago, certificationTime.value)
        is CertificationTime.Days -> stringResource(R.string.certification_time_days_ago, certificationTime.value)
        null -> ""
    }

@Preview(showBackground = true)
@Composable
private fun PhotologCardContentPreview(
    @PreviewParameter(PhotologDetailPreviewProvider::class)
    uiState: PhotologDetailUiState,
) {
    TwixTheme {
        PhotologCardContent(
            uiState = uiState.copy(isLoading = true),
            isPokeDisabled = false,
            onSwipe = {},
            onClickUpload = {},
            onPoke = {},
        )
    }
}
