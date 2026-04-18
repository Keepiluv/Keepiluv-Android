package com.twix.data.repository

import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.invitecode.InviteCode
import com.twix.domain.repository.OnBoardingRepository
import com.twix.network.execute.safeApiCall
import com.twix.network.model.request.AnniversaryRequest
import com.twix.network.model.request.CoupleConnectionRequest
import com.twix.network.model.request.ProfileRequest
import com.twix.network.service.OnboardingService
import com.twix.result.AppResult

class DefaultOnboardingRepository(
    private val service: OnboardingService,
) : OnBoardingRepository {
    override suspend fun fetchInviteCode(): AppResult<InviteCode> =
        safeApiCall {
            val response = service.fetchInviteCode()
            InviteCode(response.inviteCode)
        }

    override suspend fun fetchOnboardingStatus(): AppResult<OnboardingStatus> =
        safeApiCall {
            val response = service.fetchOnBoardingStatus()
            OnboardingStatus.from(response.status)
        }

    override suspend fun anniversarySetup(request: String): AppResult<Unit> =
        safeApiCall {
            service.anniversarySetup(AnniversaryRequest(request))
        }

    override suspend fun coupleConnection(request: String): AppResult<Unit> =
        safeApiCall { service.coupleConnection(CoupleConnectionRequest(request)) }

    override suspend fun profileSetup(request: String): AppResult<Unit> = safeApiCall { service.profileSetup(ProfileRequest(request)) }
}
