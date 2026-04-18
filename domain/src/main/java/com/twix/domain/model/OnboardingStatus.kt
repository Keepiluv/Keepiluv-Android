package com.twix.domain.model

enum class OnboardingStatus {
    COUPLE_CONNECTION,
    PROFILE_SETUP,
    ANNIVERSARY_SETUP,
    COMPLETED,
    ;

    companion object {
        fun from(status: String): OnboardingStatus =
            runCatching {
                valueOf(status.trim().uppercase())
            }.getOrElse {
                throw IllegalArgumentException(UNKNOWN_STATUS.format(status))
            }

        private const val UNKNOWN_STATUS = "UNKNOWN_STATUS: %s"
    }
}
