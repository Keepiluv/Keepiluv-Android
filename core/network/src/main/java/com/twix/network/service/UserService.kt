package com.twix.network.service

import com.twix.network.model.response.user.model.UserResponse
import de.jensklingenberg.ktorfit.http.GET

interface UserService {
    @GET("api/v1/users/me")
    suspend fun fetchUserInfo(): UserResponse
}
