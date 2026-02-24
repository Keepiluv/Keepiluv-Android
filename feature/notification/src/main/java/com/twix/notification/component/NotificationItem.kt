package com.twix.notification.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.NotificationType
import com.twix.domain.model.goal.Notification
import com.twix.ui.extension.noRippleClickable

@Composable
fun NotificationItem(
    notification: Notification,
    onClick: (Long) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(76.dp)
                .noRippleClickable { onClick(notification.id) },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(
            text = notification.title,
            style = AppTextStyle.B1,
            color = GrayColor.C500,
            maxLines = 2,
            modifier =
                Modifier
                    .weight(1f),
        )

        if (!notification.isRead) {
            Spacer(Modifier.width(28.dp))
            NewBadge()
        }
    }
}

@Composable
private fun NewBadge() {
    Box(
        modifier =
            Modifier
                .background(GrayColor.C500, RoundedCornerShape(8.dp))
                .size(width = 46.dp, height = 26.dp),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = stringResource(R.string.word_new),
            style = AppTextStyle.B4,
            color = CommonColor.White,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        NotificationItem(
            notification =
                Notification(
                    id = 1,
                    title = "titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle",
                    body = "content",
                    type = NotificationType.GOAL_COMPLETED,
                    isRead = false,
                    deepLink = null,
                    createdAt = null,
                ),
            onClick = {},
        )
    }
}
