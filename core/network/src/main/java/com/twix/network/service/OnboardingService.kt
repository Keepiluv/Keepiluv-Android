package com.twix.network.service

import com.twix.network.model.request.AnniversaryRequest
import com.twix.network.model.request.CoupleConnectionRequest
import com.twix.network.model.request.ProfileRequest
import com.twix.network.model.response.onboarding.InviteCodeResponse
import com.twix.network.model.response.onboarding.OnBoardingStatusResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST

interface OnboardingService {
    @POST("api/v1/onboarding/anniversary")
    suspend fun anniversarySetup(
        @Body request: AnniversaryRequest,
    )

    @POST("api/v1/onboarding/couple-connection")
    suspend fun coupleConnection(
        @Body request: CoupleConnectionRequest,
    )

    @POST("api/v1/onboarding/profile")
    suspend fun profileSetup(
        @Body request: ProfileRequest,
    )

    @GET("api/v1/onboarding/invite-code")
    suspend fun fetchInviteCode(): InviteCodeResponse

    @GET("api/v1/onboarding/status")
    suspend fun fetchOnBoardingStatus(): OnBoardingStatusResponse
}
