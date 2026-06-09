package com.twix.domain.model.notification

data class NotificationSettings(
    val isPushEnabled: Boolean,
    val isMarketingPushEnabled: Boolean,
    val isNightPushEnabled: Boolean,
)
