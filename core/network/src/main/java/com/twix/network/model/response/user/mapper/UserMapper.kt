package com.twix.network.model.response.user.mapper

import com.twix.domain.model.user.User
import com.twix.network.model.response.user.model.UserResponse

fun UserResponse.toDomain(): User =
    User(
        id = id,
        name = name,
        email = email,
        inviteCode = inviteCode,
    )
