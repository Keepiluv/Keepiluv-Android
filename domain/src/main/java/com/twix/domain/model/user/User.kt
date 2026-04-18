package com.twix.domain.model.user

data class User(
    val id: Long,
    val name: String,
    val email: String,
    val inviteCode: String,
)
