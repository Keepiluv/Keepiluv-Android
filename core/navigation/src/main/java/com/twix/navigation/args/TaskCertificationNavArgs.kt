package com.twix.navigation.args

import com.twix.navigation.NavRoutes
import kotlinx.serialization.Serializable

@Serializable
data class DetailNavArgs(
    val goalId: Long,
    val from: NavRoutes.TaskCertificationRoute.From,
    val photologId: Long = -1,
    val selectedDate: String = "",
    val comment: String = "",
)
