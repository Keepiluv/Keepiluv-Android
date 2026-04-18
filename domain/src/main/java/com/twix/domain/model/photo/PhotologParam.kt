package com.twix.domain.model.photo

import java.time.LocalDate

data class PhotologParam(
    val goalId: Long,
    val fileName: String,
    val comment: String,
    val verificationDate: LocalDate,
)
