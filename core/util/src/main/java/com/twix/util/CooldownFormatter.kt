package com.twix.util

object CooldownFormatter {
    fun format(remainingMs: Long): CooldownTime {
        val totalMinutes = remainingMs / 60_000
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        return CooldownTime(hours = hours, minutes = minutes)
    }
}

data class CooldownTime(
    val hours: Long,
    val minutes: Long,
)
