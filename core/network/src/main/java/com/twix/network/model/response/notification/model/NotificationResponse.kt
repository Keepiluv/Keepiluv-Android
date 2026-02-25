package com.twix.network.model.response.notification.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationResponse(
    val id: Long,
    val type: String,
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val isRead: Boolean,
    val createdAt: String,
)
