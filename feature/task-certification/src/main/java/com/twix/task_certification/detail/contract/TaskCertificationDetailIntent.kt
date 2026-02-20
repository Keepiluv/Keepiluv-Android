package com.twix.task_certification.detail.contract

import com.twix.domain.model.enums.GoalReactionType
import com.twix.ui.base.Intent

sealed interface TaskCertificationDetailIntent : Intent {
    data class Reaction(
        val type: GoalReactionType,
    ) : TaskCertificationDetailIntent

    data object Sting : TaskCertificationDetailIntent

    data object SwipeCard : TaskCertificationDetailIntent
}
