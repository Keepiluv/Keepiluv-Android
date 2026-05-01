package com.twix.domain.model.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class CertificationTimeTest {
    @Test
    fun `0분 전 인증 시간이 주어지면 JustNow를 반환한다`() {
        // given
        val now = Instant.now()
        val certifiedAt = now.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `5분 전 인증 시간이 주어지면 JustNow를 반환한다`() {
        // given
        val fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES)
        val certifiedAt = fiveMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `10분 전 인증 시간이 주어지면 JustNow를 반환한다`() {
        // given
        val tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES)
        val certifiedAt = tenMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `11분 전 인증 시간이 주어지면 11분을 반환한다`() {
        // given
        val elevenMinutesAgo = Instant.now().minus(11, ChronoUnit.MINUTES)
        val certifiedAt = elevenMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Minutes::class.java)
            .extracting { (it as CertificationTime.Minutes).value }
            .isEqualTo(11L)
    }

    @Test
    fun `30분 전 인증 시간이 주어지면 30분을 반환한다`() {
        // given
        val thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES)
        val certifiedAt = thirtyMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Minutes::class.java)
            .extracting { (it as CertificationTime.Minutes).value }
            .isEqualTo(30L)
    }

    @Test
    fun `59분 전 인증 시간이 주어지면 59분을 반환한다`() {
        // given
        val fiftyNineMinutesAgo = Instant.now().minus(59, ChronoUnit.MINUTES)
        val certifiedAt = fiftyNineMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Minutes::class.java)
            .extracting { (it as CertificationTime.Minutes).value }
            .isEqualTo(59L)
    }

    @Test
    fun `1시간 전 인증 시간이 주어지면 1시간을 반환한다`() {
        // given
        val oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS)
        val certifiedAt = oneHourAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Hours::class.java)
            .extracting { (it as CertificationTime.Hours).value }
            .isEqualTo(1L)
    }

    @Test
    fun `80분 전 인증 시간이 주어지면 1시간을 반환한다`() {
        // given
        val eightyMinutesAgo = Instant.now().minus(80, ChronoUnit.MINUTES)
        val certifiedAt = eightyMinutesAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Hours::class.java)
            .extracting { (it as CertificationTime.Hours).value }
            .isEqualTo(1L)
    }

    @Test
    fun `12시간 전 인증 시간이 주어지면 12시간을 반환한다`() {
        // given
        val twelveHoursAgo = Instant.now().minus(12, ChronoUnit.HOURS)
        val certifiedAt = twelveHoursAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Hours::class.java)
            .extracting { (it as CertificationTime.Hours).value }
            .isEqualTo(12L)
    }

    @Test
    fun `23시간 전 인증 시간이 주어지면 23시간을 반환한다`() {
        // given
        val twentyThreeHoursAgo = Instant.now().minus(23, ChronoUnit.HOURS)
        val certifiedAt = twentyThreeHoursAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Hours::class.java)
            .extracting { (it as CertificationTime.Hours).value }
            .isEqualTo(23L)
    }

    @Test
    fun `24시간 전 인증 시간이 주어지면 1일을 반환한다`() {
        // given
        val twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS)
        val certifiedAt = twentyFourHoursAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Days::class.java)
            .extracting { (it as CertificationTime.Days).value }
            .isEqualTo(1L)
    }

    @Test
    fun `3일 전 인증 시간이 주어지면 3일을 반환한다`() {
        // given
        val threeDaysAgo = Instant.now().minus(3, ChronoUnit.DAYS)
        val certifiedAt = threeDaysAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Days::class.java)
            .extracting { (it as CertificationTime.Days).value }
            .isEqualTo(3L)
    }

    @Test
    fun `7일 전 인증 시간이 주어지면 7일을 반환한다`() {
        // given
        val sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS)
        val certifiedAt = sevenDaysAgo.toString()

        // when
        val result = CertificationTime.from(certifiedAt)

        // then
        assertThat(result)
            .isInstanceOf(CertificationTime.Days::class.java)
            .extracting { (it as CertificationTime.Days).value }
            .isEqualTo(7L)
    }
}
