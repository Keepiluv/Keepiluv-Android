package com.twix.domain.model.time

sealed interface CooldownTime {
    data class Hours(
        val value: Long,
    ) : CooldownTime

    data class HoursAndMinutes(
        val hours: Long,
        val minutes: Long,
    ) : CooldownTime

    data class Minutes(
        val value: Long,
    ) : CooldownTime

    companion object {
        fun from(remainingMs: Long): CooldownTime {
            val totalMinutes = remainingMs / 60_000
            val hours = totalMinutes / 60
            val minutes = totalMinutes % 60

            return when {
                hours > 0 && minutes == 0L -> Hours(hours)
                hours > 0 -> HoursAndMinutes(hours, minutes)
                else -> Minutes(minutes)
            }
        }
    }
}
