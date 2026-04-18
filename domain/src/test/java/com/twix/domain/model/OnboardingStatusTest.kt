package com.twix.domain.model

import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import kotlin.test.assertEquals

class OnboardingStatusTest {
    @ParameterizedTest
    @CsvSource(
        "COUPLE_CONNECTION,COUPLE_CONNECTION",
        "PROFILE_SETUP,PROFILE_SETUP",
        "ANNIVERSARY_SETUP,ANNIVERSARY_SETUP",
        "COMPLETED,COMPLETED",
    )
    @DisplayName("올바른 OnboardingStatus enum을 반환한다")
    fun `유효한 상태 문자열이 주어지면 올바른 OnboardingStatus enum을 반환한다`(
        input: String,
        expected: OnboardingStatus,
    ) {
        // when
        val result = OnboardingStatus.from(input)

        // then
        assertEquals(result, expected)
    }

    @Test
    fun `유효하지 않은 상태 문자열이 주어지면 IllegalArgumentException을 던진다`() {
        // given
        val invalidStatus = "UNKNOWN"

        // when
        assertThatThrownBy { OnboardingStatus.from(invalidStatus) }
            // then
            .isInstanceOf(IllegalArgumentException::class.java)
            .hasMessage("UNKNOWN_STATUS: $invalidStatus")
    }
}
