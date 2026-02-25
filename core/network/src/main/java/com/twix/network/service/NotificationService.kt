package com.twix.network.service

import com.twix.network.model.request.notification.InitNotificationSettingsRequest
import com.twix.network.model.request.notification.RegisterFcmTokenRequest
import com.twix.network.model.request.notification.TokenRequest
import com.twix.network.model.response.notification.model.NotificationListResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query

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

    @POST("api/v1/notifications/settings/init")
    suspend fun initNotificationSettings(
        @Body request: InitNotificationSettingsRequest,
    )

    @GET("api/v1/notifications")
    suspend fun fetchNotifications(
        @Query("lastId") lastId: Long? = null,
        @Query("size") size: Int = 20,
    ): NotificationListResponse
}
