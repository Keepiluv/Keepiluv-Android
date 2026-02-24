package com.twix.domain.model.goal

import com.twix.domain.model.enums.NotificationType
import java.time.LocalDateTime

data class Notification(
    val id: Long,
    val type: NotificationType,
    val title: String,
    val body: String,
    val deepLink: String?,
    val isRead: Boolean,
    val createdAt: LocalDateTime?,
)
