package com.twix.token

interface TokenProvider {
    val accessToken: String

    val refreshToken: String

    suspend fun saveToken(
        accessToken: String,
        refreshToken: String,
    )

    suspend fun clear()

    suspend fun loadRefreshToken(): String
}
