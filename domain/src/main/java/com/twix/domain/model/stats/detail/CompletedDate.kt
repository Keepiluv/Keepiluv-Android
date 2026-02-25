package com.twix.domain.model.stats.detail

import java.time.LocalDate

data class CompletedDate(
    val date: LocalDate,
    val myImageUrl: String?,
    val partnerImageUrl: String?,
)
