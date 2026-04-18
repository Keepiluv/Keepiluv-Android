package com.twix.domain.model.enums

enum class GoalIconType {
    DEFAULT,
    CLEAN,
    EXERCISE,
    BOOK,
    PENCIL,
    HEALTH,
    HEART,
    LAPTOP,
    ;

    fun toApi(): String =
        when (this) {
            DEFAULT -> "ICON_DEFAULT"
            CLEAN -> "ICON_CLEAN"
            EXERCISE -> "ICON_EXERCISE"
            BOOK -> "ICON_BOOK"
            PENCIL -> "ICON_PENCIL"
            HEALTH -> "ICON_HEALTH"
            HEART -> "ICON_HEART"
            LAPTOP -> "ICON_LAPTOP"
        }

    companion object {
        fun fromApi(icon: String): GoalIconType =
            when (icon) {
                "ICON_DEFAULT" -> DEFAULT
                "ICON_CLEAN" -> CLEAN
                "ICON_EXERCISE" -> EXERCISE
                "ICON_BOOK" -> BOOK
                "ICON_PENCIL" -> PENCIL
                "ICON_HEALTH" -> HEALTH
                "ICON_HEART" -> HEART
                "ICON_LAPTOP" -> LAPTOP
                else -> DEFAULT
            }
    }
}
