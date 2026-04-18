package com.twix.domain.model.photolog

data class PhotoLogs(
    val targetDate: String,
    val myNickname: String,
    val partnerNickname: String,
    val goals: List<GoalPhotolog>,
)
