package com.twix.domain.model.enums

enum class RepeatCycle {
    DAILY,
    WEEKLY,
    MONTHLY,
    ;

    fun toApi(): String = name

    companion object {
        fun fromApi(value: String): RepeatCycle = runCatching { valueOf(value) }.getOrElse { DAILY }
    }
}
