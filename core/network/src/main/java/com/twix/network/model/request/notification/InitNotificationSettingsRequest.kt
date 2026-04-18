package com.twix.network.model.request.notification

import kotlinx.serialization.Serializable

@Serializable
data class InitNotificationSettingsRequest(
    val isPushEnabled: Boolean,
    val isMarketingPushEnabled: Boolean,
    val isNightPushEnabled: Boolean,
)
