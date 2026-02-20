package com.twix.designsystem.components.button.model

import androidx.compose.ui.graphics.Color
import com.twix.domain.login.LoginType

data class LoginTypeUiModel(
    val type: LoginType,
    val logo: Int,
    val title: Int,
    val background: Color,
    val border: Color,
    val textColor: Color,
)
