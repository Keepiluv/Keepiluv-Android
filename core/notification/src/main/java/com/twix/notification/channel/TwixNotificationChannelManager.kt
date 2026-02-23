package com.twix.notification.channel

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context

class TwixNotificationChannelManager(
    private val context: Context,
) {
    fun ensureDefaultChannel() {
        val manager = context.getSystemService(NotificationManager::class.java)
        val channel =
            NotificationChannel(
                CHANNEL_DEFAULT,
                "일반 알림",
                NotificationManager.IMPORTANCE_HIGH,
            ).apply {
                description = "Twix 알림 채널"
            }
        manager.createNotificationChannel(channel)
    }

    companion object {
        const val CHANNEL_DEFAULT = "twix_default_notifications"
    }
}
