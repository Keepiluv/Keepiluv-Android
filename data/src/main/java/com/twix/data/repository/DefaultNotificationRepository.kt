package com.twix.data.repository

import com.twix.domain.repository.NotificationRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.notification.InitNotificationSettingsRequest
import com.twix.network.model.request.notification.RegisterFcmTokenRequest
import com.twix.network.model.request.notification.TokenRequest
import com.twix.network.model.response.notification.mapper.toDomain
import com.twix.network.service.NotificationService

class DefaultNotificationRepository(
    private val service: NotificationService,
) : NotificationRepository {
    override suspend fun registerFcmToken(
        token: String,
        deviceId: String,
    ) = safeApiCall { service.registerFcmToken(RegisterFcmTokenRequest(token, deviceId)) }

    override suspend fun deleteFcmToken(token: String) = safeApiCall { service.deleteFcmToken(TokenRequest(token)) }

    override suspend fun markNotificationAsRead(notificationId: Long) = safeApiCall { service.markNotificationAsRead(notificationId) }

    override suspend fun initNotificationSettings(
        isPushEnabled: Boolean,
        isMarketingPushEnabled: Boolean,
        isNightPushEnabled: Boolean,
    ) = safeApiCall {
        service.initNotificationSettings(
            InitNotificationSettingsRequest(
                isPushEnabled = isPushEnabled,
                isMarketingPushEnabled = isMarketingPushEnabled,
                isNightPushEnabled = isNightPushEnabled,
            ),
        )
    }

    override suspend fun fetchNotifications(
        lastId: Long?,
        size: Int,
    ) = safeApiCall {
        service
            .fetchNotifications(
                lastId = lastId,
                size = size,
            ).toDomain()
    }

    override suspend fun markAllNotificationsAsRead() = safeApiCall { service.markAllNotificationsAsRead() }
}
