package com.twix.util

import java.time.Duration
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime

object RelativeTimeFormatter {
    fun format(uploadedAt: String): String =
        runCatching {
            val uploaded = Instant.parse(uploadedAt)
            val now = Instant.now()

            val duration = Duration.between(uploaded, now)

            val minutes = duration.toMinutes()
            val hours = duration.toHours()
            val days = duration.toDays()

            when {
                minutes < 1 -> "방금 전"
                minutes < 60 -> "${minutes}분 전"
                hours < 24 -> "${hours}시간 전"
                days < 7 -> "${days}일 전"
                else -> {
                    val date = ZonedDateTime.ofInstant(uploaded, ZoneId.systemDefault())
                    "${date.monthValue}.${date.dayOfMonth}"
                }
            }
        }.getOrDefault("")
}
