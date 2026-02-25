package com.twix.notification.component

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.NotificationType
import com.twix.domain.model.notification.Notification

@Composable
fun NotificationList(
    modifier: Modifier = Modifier,
    notificationsList: List<Notification>,
    listState: LazyListState,
    onNotificationClick: (Long) -> Unit,
) {
    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp),
        state = listState,
    ) {
        itemsIndexed(
            items = notificationsList,
            key = { _, item -> item.id },
        ) { index, notification ->
            NotificationItem(
                notification = notification,
                onClick = onNotificationClick,
            )

            if (index != notificationsList.lastIndex) {
                HorizontalDivider(thickness = 1.dp, color = GrayColor.C100)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        NotificationList(
            notificationsList =
                listOf(
                    Notification(
                        id = 1,
                        title = "titletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitletitle",
                        body = "content",
                        type = NotificationType.GOAL_COMPLETED,
                        isRead = false,
                        deepLink = null,
                        createdAt = null,
                    ),
                    Notification(
                        id = 2,
                        title = "titletitletitletitletitletitletitletitletitletitle",
                        body = "content",
                        type = NotificationType.GOAL_COMPLETED,
                        isRead = false,
                        deepLink = null,
                        createdAt = null,
                    ),
                ),
            onNotificationClick = {},
            listState = LazyListState(),
        )
    }
}
