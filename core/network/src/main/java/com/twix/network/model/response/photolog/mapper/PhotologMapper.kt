package com.twix.network.model.response.photolog.mapper

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photolog.GoalPhotolog
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.model.photolog.PhotologDetail
import com.twix.network.model.response.photolog.GoalPhotologResponse
import com.twix.network.model.response.photolog.PhotoLogsResponse
import com.twix.network.model.response.photolog.PhotologDetailResponse

fun PhotoLogsResponse.toDomain(): PhotoLogs =
    PhotoLogs(
        targetDate = targetDate,
        myNickname = myNickname,
        partnerNickname = partnerNickname,
        goals = photologs.map { it.toDomain() },
    )

private fun GoalPhotologResponse.toDomain(): GoalPhotolog =
    GoalPhotolog(
        goalId = goalId,
        goalName = goalName,
        icon = GoalIconType.fromApi(goalIcon),
        myPhotolog = myPhotolog?.toDomain(),
        partnerPhotolog = partnerPhotolog?.toDomain(),
    )

private fun PhotologDetailResponse.toDomain(): PhotologDetail =
    PhotologDetail(
        photologId = photologId,
        goalId = goalId,
        imageUrl = imageUrl,
        comment = comment,
        verificationDate = verificationDate,
        uploaderName = uploaderName,
        uploadedAt = uploadedAt,
        reaction = GoalReactionType.fromApi(reaction),
    )
