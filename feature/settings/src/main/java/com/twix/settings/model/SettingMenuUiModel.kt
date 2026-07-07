package com.twix.settings.model

data class SettingMenuUiModel(
    val iconResId: Int,
    val title: String,
    val trailingText: String? = null,
    val trailingIconResId: Int? = null,
    val onClick: () -> Unit,
)
