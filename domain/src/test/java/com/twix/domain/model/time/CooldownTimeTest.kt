package com.twix.domain.model.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CooldownTimeTest {
    @Test
    fun `0분이 주어지면 MinutesOnly 0분을 반환한다`() {
        // given
        val remainingMs = 0L

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result)
            .isInstanceOf(CooldownTime.Minutes::class.java)
            .extracting { (it as CooldownTime.Minutes).value }
            .isEqualTo(0L)
    }

    @Test
    fun `30분이 주어지면 MinutesOnly 30분을 반환한다`() {
        // given
        val remainingMs = 1_800_000L // 30분 * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result)
            .isInstanceOf(CooldownTime.Minutes::class.java)
            .extracting { (it as CooldownTime.Minutes).value }
            .isEqualTo(30L)
    }

    @Test
    fun `59분이 주어지면 MinutesOnly 59분을 반환한다`() {
        // given
        val remainingMs = 3_540_000L // 59분 * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result)
            .isInstanceOf(CooldownTime.Minutes::class.java)
            .extracting { (it as CooldownTime.Minutes).value }
            .isEqualTo(59L)
    }

    @Test
    fun `1시간 0분이 주어지면 HoursOnly 1시간을 반환한다`() {
        // given
        val remainingMs = 3_600_000L // 1시간 * 60분 * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result)
            .isInstanceOf(CooldownTime.Hours::class.java)
            .extracting { (it as CooldownTime.Hours).value }
            .isEqualTo(1L)
    }

    @Test
    fun `2시간 0분이 주어지면 HoursOnly 2시간을 반환한다`() {
        // given
        val remainingMs = 7_200_000L // 2시간 * 60분 * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result)
            .isInstanceOf(CooldownTime.Hours::class.java)
            .extracting { (it as CooldownTime.Hours).value }
            .isEqualTo(2L)
    }

    @Test
    fun `1시간 30분이 주어지면 HoursAndMinutes 1시간 30분을 반환한다`() {
        // given
        val remainingMs = 5_400_000L // (1시간 30분) * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result).isInstanceOf(CooldownTime.HoursAndMinutes::class.java)
        val hoursAndMinutes = result as CooldownTime.HoursAndMinutes
        assertThat(hoursAndMinutes.hours).isEqualTo(1L)
        assertThat(hoursAndMinutes.minutes).isEqualTo(30L)
    }

    @Test
    fun `2시간 15분이 주어지면 HoursAndMinutes 2시간 15분을 반환한다`() {
        // given
        val remainingMs = 8_100_000L // (2시간 15분) * 60초 * 1000ms

        // when
        val result = CooldownTime.from(remainingMs)

        // then
        assertThat(result).isInstanceOf(CooldownTime.HoursAndMinutes::class.java)
        val hoursAndMinutes = result as CooldownTime.HoursAndMinutes
        assertThat(hoursAndMinutes.hours).isEqualTo(2L)
        assertThat(hoursAndMinutes.minutes).isEqualTo(15L)
    }
}
