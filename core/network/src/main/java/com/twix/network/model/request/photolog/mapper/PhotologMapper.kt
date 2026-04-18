package com.twix.network.model.request.photolog.mapper

import com.twix.domain.model.photo.PhotologParam
import com.twix.network.model.request.photolog.model.PhotologRequest

fun PhotologParam.toRequest(): PhotologRequest =
    PhotologRequest(
        goalId = goalId,
        fileName = fileName,
        comment = comment,
        verificationDate = verificationDate.toString(),
    )
