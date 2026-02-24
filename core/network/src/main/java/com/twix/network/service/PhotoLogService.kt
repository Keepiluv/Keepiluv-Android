package com.twix.network.service

import com.twix.network.model.request.ReactionRequest
import com.twix.network.model.request.photolog.model.PhotologModifyRequest
import com.twix.network.model.request.photolog.model.PhotologRequest
import com.twix.network.model.response.photo.model.PhotoLogUploadUrlResponse
import com.twix.network.model.response.photolog.PhotoLogsResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import java.time.LocalDate

interface PhotoLogService {
    @GET("api/v1/photologs/upload-url")
    suspend fun getUploadUrl(
        @Query("goalId") goalId: Long,
    ): PhotoLogUploadUrlResponse

    @POST("api/v1/photologs")
    suspend fun uploadPhotoLog(
        @Body request: PhotologRequest,
    )

    @GET("api/v1/photologs")
    suspend fun fetchPhotoLogs(
        @Query("targetDate") request: LocalDate,
    ): PhotoLogsResponse

    @PUT("api/v1/photologs/{photologId}/reaction")
    suspend fun reactToPhotolog(
        @Path("photologId") photologId: Long,
        @Body request: ReactionRequest,
    )

    @PUT("api/v1/photologs/{photologId}")
    suspend fun modifyPhotolog(
        @Path("photologId") photologId: Long,
        @Body request: PhotologModifyRequest,
    )
}
