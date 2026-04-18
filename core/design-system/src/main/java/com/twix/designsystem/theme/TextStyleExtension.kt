package com.twix.designsystem.theme

import androidx.compose.ui.text.TextStyle
import com.twix.domain.model.enums.AppTextStyle

fun AppTextStyle.toTextStyle(typography: AppTypography): TextStyle =
    when (this) {
        AppTextStyle.H1 -> typography.h1
        AppTextStyle.H2 -> typography.h2
        AppTextStyle.H3 -> typography.h3
        AppTextStyle.H3Brand -> typography.h3Brand
        AppTextStyle.H4Brand -> typography.h4Brand
        AppTextStyle.T1 -> typography.t1
        AppTextStyle.T2 -> typography.t2
        AppTextStyle.T3 -> typography.t3
        AppTextStyle.B1 -> typography.b1
        AppTextStyle.B2 -> typography.b2
        AppTextStyle.B3 -> typography.b3
        AppTextStyle.B4 -> typography.b4
        AppTextStyle.C1 -> typography.c1
        AppTextStyle.C2 -> typography.c2
    }
