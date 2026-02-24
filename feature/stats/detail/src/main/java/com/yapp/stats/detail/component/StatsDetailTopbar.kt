package com.yapp.stats.detail.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.popup.CommonPopup
import com.twix.designsystem.components.popup.CommonPopupDivider
import com.twix.designsystem.components.popup.CommonPopupItem
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun StatsDetailTopbar(
    goalName: String,
    isInProgressStatsDetail: Boolean,
    popupMenuVisibility: Boolean,
    onBack: () -> Unit,
    onClickAction: () -> Unit,
    onClickPopupEdit: () -> Unit,
    onClickPopupEnd: () -> Unit,
    onClickPopupDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    CommonTopBar(
        title = goalName,
        left = {
            Image(
                painter = painterResource(R.drawable.ic_arrow3_left),
                contentDescription = "back",
                modifier =
                    Modifier
                        .padding(18.dp)
                        .size(24.dp)
                        .noRippleClickable(onClick = onBack),
            )
        },
        right = {
            if (isInProgressStatsDetail) {
                PopupMenu(
                    popupMenuVisibility = popupMenuVisibility,
                    onClickAction = onClickAction,
                    onDismiss = onDismiss,
                    onEdit = onClickPopupEdit,
                    onEnd = onClickPopupEnd,
                    onDelete = onClickPopupDelete,
                )
            } else {
                DeleteButton(onClick = onClickAction)
            }
        },
    )
}

@Composable
private fun PopupMenu(
    popupMenuVisibility: Boolean,
    onClickAction: () -> Unit,
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onEnd: () -> Unit,
    onDelete: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_meatball),
            contentDescription = null,
            modifier =
                Modifier
                    .size(24.dp)
                    .rotate(90f)
                    .noRippleClickable(onClick = onClickAction),
        )

        CommonPopup(
            visible = popupMenuVisibility,
            anchorOffset = IntOffset(x = -100, y = 140),
            onDismiss = onDismiss,
        ) {
            Column(
                modifier =
                    Modifier
                        .width(88.dp)
                        .background(CommonColor.White, RoundedCornerShape(12.dp))
                        .border(1.dp, GrayColor.C500, RoundedCornerShape(12.dp)),
            ) {
                CommonPopupItem(
                    text = stringResource(R.string.action_edit),
                    onClick = onEdit,
                )
                CommonPopupDivider()
                CommonPopupItem(
                    text = stringResource(R.string.action_finish),
                    onClick = onEnd,
                )
                CommonPopupDivider()
                CommonPopupItem(
                    text = stringResource(R.string.action_delete),
                    onClick = onDelete,
                )
            }
        }
    }
}

@Composable
private fun DeleteButton(onClick: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(GrayColor.C100)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = stringResource(R.string.word_delete),
            style = AppTextStyle.T2,
            color = GrayColor.C500,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun StatsDetailTopbarPreview() {
    TwixTheme {
        StatsDetailTopbar(
            goalName = "Goal Name",
            isInProgressStatsDetail = true,
            popupMenuVisibility = true,
            onBack = {},
            onClickAction = {},
            onDismiss = {},
            onClickPopupEdit = {},
            onClickPopupEnd = {},
            onClickPopupDelete = {},
        )
    }
}
