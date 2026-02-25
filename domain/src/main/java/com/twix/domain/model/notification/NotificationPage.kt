package com.twix.domain.model.notification

data class NotificationPage(
    val notifications: List<Notification>,
    val hasNext: Boolean,
)
