package com.twix.network.model.response.notification.model

import kotlinx.serialization.Serializable

@Serializable
data class NotificationSettingsResponse(
    val isPushEnabled: Boolean,
    val isMarketingPushEnabled: Boolean,
    val isNightPushEnabled: Boolean,
)
