package com.twix.designsystem.components.button.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.twix.domain.login.LoginType

data class LoginTypeUiModel(
    val type: LoginType,
    val logo: ImageVector,
    val title: String,
    val background: Color,
    val border: Color,
    val textColor: Color,
)
