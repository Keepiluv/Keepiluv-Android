package com.twix.designsystem.components.toast.model

import androidx.compose.runtime.Immutable

@Immutable
data class ToastData(
    val message: String,
    val type: ToastType,
    val durationMillis: Long = 2_000L,
    val action: ToastAction? = null,
)
