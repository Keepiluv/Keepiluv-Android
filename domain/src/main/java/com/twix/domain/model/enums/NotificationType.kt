package com.twix.domain.model.enums

enum class NotificationType {
    PARTNER_CONNECTED,
    POKE,
    GOAL_COMPLETED,
    REACTION,
    DAILY_GOAL_ACHIEVED,
    GOAL_ENDED,
    UNKNOWN,
    ;

    companion object {
        fun fromApi(value: String): NotificationType = entries.firstOrNull { it.name == value } ?: UNKNOWN
    }
}
