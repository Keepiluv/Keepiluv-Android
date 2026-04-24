package com.twix.domain.model.time

sealed interface CooldownTime {
    data class HoursAndMinutes(
        val hours: Long,
        val minutes: Long,
    ) : CooldownTime

    data class MinutesOnly(
        val minutes: Long,
    ) : CooldownTime

    companion object {
        fun from(remainingMs: Long): CooldownTime {
            val totalMinutes = remainingMs / 60_000
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60

            return when {
                hours > 0 -> HoursAndMinutes(hours, minutes)
                else -> MinutesOnly(minutes)
            }
        }
    }
}
