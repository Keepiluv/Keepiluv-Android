package com.twix.domain.model.time

import java.time.Duration
import java.time.Instant

sealed interface CertificationTime {
    data object JustNow : CertificationTime

    data class Minutes(
        val value: Long,
    ) : CertificationTime

    data class Hours(
        val value: Long,
    ) : CertificationTime

    data class Days(
        val value: Long,
    ) : CertificationTime

    companion object {
        fun from(certifiedAt: String): CertificationTime {
            val certified = Instant.parse(certifiedAt)
            val now = Instant.now()
            val duration = Duration.between(certified, now)

            val minutes = duration.toMinutes()
            val hours = duration.toHours()
            val days = duration.toDays()

            return when {
                minutes <= 10 -> JustNow
                minutes < 60 -> Minutes(minutes)
                hours < 24 -> Hours(hours)
                else -> Days(days)
            }
        }
    }
}
