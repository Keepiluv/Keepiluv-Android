package com.twix.navigation.args

import com.twix.navigation.NavRoutes
import kotlinx.serialization.Serializable

// TODO("인증샷 단일 조회 API 연동시 제거")
@Serializable
data class EditorNavArgs(
    val goalId: Long,
    val goalName: String,
    val nickname: String,
    val photologId: Long,
    val imageUrl: String,
    val comment: String?,
)

@Serializable
data class DetailNavArgs(
    val goalId: Long,
    val from: NavRoutes.TaskCertificationRoute.From,
    val photologId: Long = -1,
    val selectedDate: String = "",
    val comment: String = "",
)
