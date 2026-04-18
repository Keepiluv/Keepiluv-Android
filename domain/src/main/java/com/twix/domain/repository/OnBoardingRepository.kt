package com.twix.domain.repository

import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.invitecode.InviteCode
import com.twix.result.AppResult

interface OnBoardingRepository {
    suspend fun anniversarySetup(request: String): AppResult<Unit>

    suspend fun coupleConnection(request: String): AppResult<Unit>

    suspend fun profileSetup(request: String): AppResult<Unit>

    suspend fun fetchInviteCode(): AppResult<InviteCode>

    suspend fun fetchOnboardingStatus(): AppResult<OnboardingStatus>
}
