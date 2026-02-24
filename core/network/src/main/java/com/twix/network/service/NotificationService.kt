package com.twix.network.service

import com.twix.network.model.request.notification.RegisterFcmTokenRequest
import com.twix.network.model.request.notification.TokenRequest
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface NotificationService {
    @POST("api/v1/notifications/fcm-token")
    suspend fun registerFcmToken(
        @Body request: RegisterFcmTokenRequest,
    )

    @DELETE("api/v1/notifications/fcm-token")
    suspend fun deleteFcmToken(
        @Body request: TokenRequest,
    )

    @PATCH("api/v1/notifications/{notificationId}/read")
    suspend fun markNotificationAsRead(
        @Path("notificationId") notificationId: Long,
    )
}
