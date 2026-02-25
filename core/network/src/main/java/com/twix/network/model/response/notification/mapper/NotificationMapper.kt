package com.twix.network.model.response.notification.mapper

import com.twix.domain.model.enums.NotificationType
import com.twix.domain.model.notification.Notification
import com.twix.domain.model.notification.NotificationPage
import com.twix.network.model.response.notification.model.NotificationListResponse
import com.twix.network.model.response.notification.model.NotificationResponse
import java.time.LocalDateTime
import java.time.OffsetDateTime

fun NotificationListResponse.toDomain(): NotificationPage =
    NotificationPage(
        notifications = notifications.map { it.toDomain() },
        hasNext = hasNext,
    )

fun NotificationResponse.toDomain(): Notification =
    Notification(
        id = id,
        type = NotificationType.fromApi(type),
        title = title,
        body = body,
        deepLink = deepLink,
        isRead = isRead,
        createdAt = createdAt.toLocalDateTimeOrNull(),
    )

private fun String.toLocalDateTimeOrNull(): LocalDateTime? = runCatching { OffsetDateTime.parse(this).toLocalDateTime() }.getOrNull()
