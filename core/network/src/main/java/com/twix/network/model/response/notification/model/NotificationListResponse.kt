package com.twix.network.model.response.notification.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationListResponse(
    val notifications: List<NotificationResponse>,
    val hasNext: Boolean,
)
