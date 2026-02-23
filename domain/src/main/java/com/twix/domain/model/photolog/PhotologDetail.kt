package com.twix.domain.model.photolog

import com.twix.domain.model.enums.GoalReactionType

data class PhotologDetail(
    val photologId: Long,
    val goalId: Long,
    val imageUrl: String,
    val verificationDate: String,
    val uploaderName: String,
    val uploadedAt: String,
    val comment: String?,
    val reaction: GoalReactionType?,
) {
    fun updateReaction(reaction: GoalReactionType): PhotologDetail = copy(reaction = reaction)
}
