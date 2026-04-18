package com.twix.data.repository

import com.twix.domain.model.user.User
import com.twix.domain.repository.UserRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.response.user.mapper.toDomain
import com.twix.network.service.UserService
import com.twix.result.AppResult

class DefaultUserRepository(
    private val service: UserService,
) : UserRepository {
    override suspend fun fetchUserInfo(): AppResult<User> = safeApiCall { service.fetchUserInfo().toDomain() }
}
