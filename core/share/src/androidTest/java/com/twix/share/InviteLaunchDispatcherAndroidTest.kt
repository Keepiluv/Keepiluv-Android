package com.twix.share

import android.content.Intent
import android.net.Uri
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class InviteLaunchDispatcherAndroidTest {
    @Test
    fun teamTwixInviteUrlIntentDispatchesInviteCode() {
        val dispatcher = InviteLaunchDispatcher()
        val intent =
            Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://keepiluv.teamtwix.com/invite?code=$INVITE_CODE")
            }

        dispatcher.dispatchFromIntent(intent)

        assertEquals(INVITE_CODE, dispatcher.pendingInviteCode.value)
    }

    private companion object {
        const val INVITE_CODE = "KP77DEPR"
    }
}
