package com.twix.navigation_contract

import android.content.Intent
import kotlinx.coroutines.flow.StateFlow

interface InviteLaunchEventSource {
    val pendingInviteCode: StateFlow<String?>

    fun dispatchFromIntent(intent: Intent?)

    fun consumePendingInviteCode()

    companion object {
        const val INVITE_SCHEME = "twix"
        const val INVITE_HOST = "invite"
        const val INVITE_CODE_PARAM = "code"
        const val PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.yapp.twix"
        const val INVITE_WEB_HOST = "keepiluv.web.app"

        fun buildInviteDeepLink(inviteCode: String) =
            "https://$INVITE_WEB_HOST?$INVITE_CODE_PARAM=$inviteCode"
    }
}
