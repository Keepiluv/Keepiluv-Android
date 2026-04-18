package com.twix.login.google

sealed interface GoogleCredentialResult {
    data class Success(
        val idToken: String,
    ) : GoogleCredentialResult

    data class Failure(
        val exception: Throwable?,
    ) : GoogleCredentialResult

    data object Cancel : GoogleCredentialResult
}
