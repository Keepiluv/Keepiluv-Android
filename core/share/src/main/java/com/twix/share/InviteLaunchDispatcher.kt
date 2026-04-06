package com.twix.share

import android.content.Intent
import android.net.Uri
import com.twix.navigation_contract.InviteLaunchEventSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InviteLaunchDispatcher : InviteLaunchEventSource {
    private val _pendingInviteCode = MutableStateFlow<String?>(null)
    override val pendingInviteCode: StateFlow<String?> = _pendingInviteCode.asStateFlow()

    override fun dispatchFromIntent(intent: Intent?) {
        if (intent?.action != Intent.ACTION_VIEW) return
        val uri = intent.data ?: return

        if (!checkCustomScheme(uri) && !checkAppLink(uri)) return

        val inviteCode =
            uri
                .getQueryParameter(INVITE_CODE_PARAM)
                ?.takeIf { it.isNotBlank() } ?: return
        _pendingInviteCode.value = inviteCode
    }

    private fun checkCustomScheme(uri: Uri) = uri.scheme == INVITE_SCHEME && uri.host == INVITE_HOST

    private fun checkAppLink(uri: Uri) =
        (uri.scheme == HTTP_SCHEME || uri.scheme == HTTPS_SCHEME) &&
            uri.host == INVITE_WEB_HOST

    override fun consumePendingInviteCode() {
        _pendingInviteCode.value = null
    }

    companion object {
        private const val INVITE_SCHEME = "twix"
        private const val INVITE_HOST = "invite"
        private const val INVITE_CODE_PARAM = "code"
        private const val INVITE_WEB_HOST = "keepiluv.web.app"
        private const val HTTP_SCHEME = "http"
        private const val HTTPS_SCHEME = "https"

        fun buildInviteDeepLink(inviteCode: String) = "https://$INVITE_WEB_HOST?$INVITE_CODE_PARAM=$inviteCode"
    }
}
