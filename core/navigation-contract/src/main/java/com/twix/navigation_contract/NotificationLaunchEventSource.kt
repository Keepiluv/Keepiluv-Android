package com.twix.navigation_contract

import android.content.Intent
import kotlinx.coroutines.flow.StateFlow

/**
 * 구현체는 :core:notification -> NotificationLaunchDispatcher
 * */
interface NotificationLaunchEventSource {
    val pendingDeepLink: StateFlow<String?>

    fun dispatchFromIntent(intent: Intent?)

    fun consumePendingDeepLink(expected: String? = null)
}
