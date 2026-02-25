package com.twix.domain.model.enums

enum class StatsStatus {
    NOT_STARTED,
    IN_PROGRESS,
    COMPLETED,
    DELETED,
    ;

    fun toApi(): String = name

    companion object {
        fun fromApi(value: String): StatsStatus = runCatching { valueOf(value) }.getOrElse { IN_PROGRESS }
    }
}
