package com.twix.domain.repository

import com.twix.domain.model.notification.NotificationPage
import com.twix.domain.model.notification.NotificationSettings
import com.twix.result.AppResult

interface NotificationRepository {
    suspend fun registerFcmToken(
        token: String,
        deviceId: String,
    ): AppResult<Unit>

    suspend fun deleteFcmToken(token: String): AppResult<Unit>

    suspend fun markNotificationAsRead(notificationId: Long): AppResult<Unit>

    suspend fun initNotificationSettings(
        isPushEnabled: Boolean,
        isMarketingPushEnabled: Boolean,
        isNightPushEnabled: Boolean,
    ): AppResult<Unit>

    suspend fun fetchNotificationSettings(): AppResult<NotificationSettings>

    suspend fun updatePokeNotificationSetting(enabled: Boolean): AppResult<NotificationSettings>

    suspend fun updateMarketingNotificationSetting(enabled: Boolean): AppResult<NotificationSettings>

    suspend fun updateNightNotificationSetting(enabled: Boolean): AppResult<NotificationSettings>

    suspend fun fetchNotifications(
        lastId: Long? = null,
        size: Int = 20,
    ): AppResult<NotificationPage>

    suspend fun markAllNotificationsAsRead(): AppResult<Unit>
}
