package com.twix.domain.model

import com.twix.domain.model.invitecode.InviteCode
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class InviteCodeTest {
    @Test
    fun `유효한 초대 코드는 정상적으로 생성된다`() {
        val inviteCode = InviteCode("AB12CD34")

        assertThat(inviteCode.value).isEqualTo("AB12CD34")
    }

    @ParameterizedTest
    @ValueSource(
        strings = [
            "ABC123",
            "AB12CD345",
        ],
    )
    fun `8자리가 아닌 초대 코드는 예외가 발생한다`(invalidLengthCode: String) {
        // when
        val result = InviteCode.create(invalidLengthCode)

        // then
        assertThat(result.isFailure).isTrue()
    }
}
