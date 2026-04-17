package com.twix.navigation_contract

import android.content.Intent
import kotlinx.coroutines.flow.StateFlow

interface InviteLaunchEventSource {
    val pendingInviteCode: StateFlow<String?>

    fun dispatchFromIntent(intent: Intent?)

    fun consumePendingInviteCode()
}
