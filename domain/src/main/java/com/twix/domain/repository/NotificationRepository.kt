package com.twix.domain.repository

import com.twix.result.AppResult

interface NotificationRepository {
    suspend fun registerFcmToken(
        token: String,
        deviceId: String,
    ): AppResult<Unit>

    suspend fun deleteFcmToken(token: String): AppResult<Unit>

    suspend fun markNotificationAsRead(notificationId: Long): AppResult<Unit>
}
