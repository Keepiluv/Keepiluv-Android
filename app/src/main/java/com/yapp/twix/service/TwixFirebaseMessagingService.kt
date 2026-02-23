package com.yapp.twix.service

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.net.toUri
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.twix.designsystem.R
import com.twix.notification.channel.TwixNotificationChannelManager
import com.twix.notification.model.PushPayload
import com.twix.notification.model.toTwixPushPayload
import com.twix.notification.routing.NotificationLaunchDispatcher
import com.twix.notification.token.NotificationTokenRegistrar
import com.yapp.twix.main.MainActivity
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TwixFirebaseMessagingService :
    FirebaseMessagingService(),
    KoinComponent {
    private val tokenRegistrar: NotificationTokenRegistrar by inject()
    private val notificationChannelManager: TwixNotificationChannelManager by inject()

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // 토큰 갱신 시 재등록
        tokenRegistrar.registerFcmToken(token)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val payload = message.data.toTwixPushPayload()

        // 앱 실행 중에 토스트나 인앱 배너를 렌더링할 때 여기에서 분기처리하면 됨
        showSystemNotification(payload)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    private fun showSystemNotification(payload: PushPayload) {
        val manager = NotificationManagerCompat.from(this)
        if (!manager.areNotificationsEnabled()) return

        notificationChannelManager.ensureDefaultChannel()

        val intent =
            Intent(this, MainActivity::class.java).apply {
                action = NotificationLaunchDispatcher.ACTION_NOTIFICATION_CLICK
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                putExtra(NotificationLaunchDispatcher.EXTRA_DEEP_LINK, payload.deepLink)
                putExtra(NotificationLaunchDispatcher.EXTRA_FROM_PUSH_CLICK, true)
            }

        val stableId = payload.resolveStableNotificationId()
        val pendingIntent =
            PendingIntent.getActivity(
                this,
                stableId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notification =
            NotificationCompat
                .Builder(this, TwixNotificationChannelManager.CHANNEL_DEFAULT)
                .setSmallIcon(R.drawable.ic_app_logo)
                .setContentTitle(payload.title ?: getString(com.yapp.twix.R.string.app_name))
                .setContentText(payload.body.orEmpty())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

        NotificationManagerCompat
            .from(this)
            .notify(stableId, notification)
    }

    // PendingIntent의 requestCode에 활용될 안전한 notificationId 변환 메서드
    private fun PushPayload.resolveStableNotificationId(): Int {
        val fromDeepLink =
            deepLink
                ?.let {
                    try {
                        it.toUri()
                    } catch (e: Exception) {
                        null
                    }
                }?.getQueryParameter("notificationId")
                ?.toLongOrNull()

        if (fromDeepLink != null) {
            val normalized = (fromDeepLink % Int.MAX_VALUE).toInt()
            return if (normalized <= 0) 1 else normalized
        }

        val fallbackSeed = deepLink ?: title ?: body ?: System.currentTimeMillis().toString()
        val hash = fallbackSeed.hashCode()
        return if (hash == Int.MIN_VALUE) 0 else kotlin.math.abs(hash)
    }
}
