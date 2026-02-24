package com.twix.domain.model.stats

import com.twix.domain.model.enums.StampColor

data class ParticipantStats(
    val nickname: String,
    val completedConut: Int,
    val stampColors: List<StampColor>,
)
