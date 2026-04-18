package com.twix.domain.repository

import com.twix.domain.model.user.User
import com.twix.result.AppResult

interface UserRepository {
    suspend fun fetchUserInfo(): AppResult<User>
}
