package com.twix.domain.model.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant
import java.time.temporal.ChronoUnit

class CertificationTimeTest {
    @Test
    fun `0분 전 인증은 JustNow를 반환한다`() {
        val now = Instant.now()
        val certifiedAt = now.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `5분 전 인증은 JustNow를 반환한다`() {
        val fiveMinutesAgo = Instant.now().minus(5, ChronoUnit.MINUTES)
        val certifiedAt = fiveMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `10분 전 인증은 JustNow를 반환한다`() {
        val tenMinutesAgo = Instant.now().minus(10, ChronoUnit.MINUTES)
        val certifiedAt = tenMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isEqualTo(CertificationTime.JustNow)
    }

    @Test
    fun `11분 전 인증은 Minutes를 반환한다`() {
        val elevenMinutesAgo = Instant.now().minus(11, ChronoUnit.MINUTES)
        val certifiedAt = elevenMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Minutes::class.java)
        assertThat((result as CertificationTime.Minutes).value).isEqualTo(11)
    }

    @Test
    fun `30분 전 인증은 Minutes를 반환한다`() {
        val thirtyMinutesAgo = Instant.now().minus(30, ChronoUnit.MINUTES)
        val certifiedAt = thirtyMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Minutes::class.java)
        assertThat((result as CertificationTime.Minutes).value).isEqualTo(30)
    }

    @Test
    fun `59분 전 인증은 Minutes를 반환한다`() {
        val fiftyNineMinutesAgo = Instant.now().minus(59, ChronoUnit.MINUTES)
        val certifiedAt = fiftyNineMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Minutes::class.java)
        assertThat((result as CertificationTime.Minutes).value).isEqualTo(59)
    }

    @Test
    fun `1시간 전 인증은 Hours를 반환한다`() {
        val oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS)
        val certifiedAt = oneHourAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Hours::class.java)
        assertThat((result as CertificationTime.Hours).value).isEqualTo(1)
    }

    @Test
    fun `80분 전 인증은 1시간으로 반환한다`() {
        val eightyMinutesAgo = Instant.now().minus(80, ChronoUnit.MINUTES)
        val certifiedAt = eightyMinutesAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Hours::class.java)
        assertThat((result as CertificationTime.Hours).value).isEqualTo(1)
    }

    @Test
    fun `12시간 전 인증은 Hours를 반환한다`() {
        val twelveHoursAgo = Instant.now().minus(12, ChronoUnit.HOURS)
        val certifiedAt = twelveHoursAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Hours::class.java)
        assertThat((result as CertificationTime.Hours).value).isEqualTo(12)
    }

    @Test
    fun `23시간 전 인증은 Hours를 반환한다`() {
        val twentyThreeHoursAgo = Instant.now().minus(23, ChronoUnit.HOURS)
        val certifiedAt = twentyThreeHoursAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Hours::class.java)
        assertThat((result as CertificationTime.Hours).value).isEqualTo(23)
    }

    @Test
    fun `24시간 전 인증은 Days를 반환한다`() {
        val twentyFourHoursAgo = Instant.now().minus(24, ChronoUnit.HOURS)
        val certifiedAt = twentyFourHoursAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Days::class.java)
        assertThat((result as CertificationTime.Days).value).isEqualTo(1)
    }

    @Test
    fun `3일 전 인증은 Days를 반환한다`() {
        val threeDaysAgo = Instant.now().minus(3, ChronoUnit.DAYS)
        val certifiedAt = threeDaysAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Days::class.java)
        assertThat((result as CertificationTime.Days).value).isEqualTo(3)
    }

    @Test
    fun `7일 전 인증은 Days를 반환한다`() {
        val sevenDaysAgo = Instant.now().minus(7, ChronoUnit.DAYS)
        val certifiedAt = sevenDaysAgo.toString()

        val result = CertificationTime.from(certifiedAt)

        assertThat(result).isInstanceOf(CertificationTime.Days::class.java)
        assertThat((result as CertificationTime.Days).value).isEqualTo(7)
    }
}
