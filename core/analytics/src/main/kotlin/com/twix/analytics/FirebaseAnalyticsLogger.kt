package com.twix.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

internal class FirebaseAnalyticsLogger(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsLogger {
    override fun log(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.name, event.parameters.toBundle())
    }

    override fun setUserId(userId: String?) {
        firebaseAnalytics.setUserId(userId)
    }

    override fun setUserProperty(
        name: String,
        value: String?,
    ) {
        firebaseAnalytics.setUserProperty(name, value)
    }
}

private fun Map<String, Any?>.toBundle(): Bundle =
    Bundle().apply {
        forEach { (key, value) ->
            when (value) {
                is String -> putString(key, value)
                is Byte -> putLong(key, value.toLong())
                is Short -> putLong(key, value.toLong())
                is Int -> putLong(key, value.toLong())
                is Long -> putLong(key, value)
                is Float -> putDouble(key, value.toDouble())
                is Double -> putDouble(key, value)
                is Boolean -> putLong(key, if (value) 1L else 0L)
                null -> Unit
            }
        }
    }
