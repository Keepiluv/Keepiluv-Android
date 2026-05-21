package com.twix.share

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class InviteLaunchDispatcherTest {
    @Test
    fun `초대 딥링크는 팀트윅스 도메인의 invite URL로 생성된다`() {
        val deepLink = InviteLaunchDispatcher.buildInviteDeepLink(INVITE_CODE)

        assertThat(deepLink).isEqualTo("https://keepiluv.teamtwix.com/invite?code=$INVITE_CODE")
    }

    private companion object {
        const val INVITE_CODE = "KP77DEPR"
    }
}
