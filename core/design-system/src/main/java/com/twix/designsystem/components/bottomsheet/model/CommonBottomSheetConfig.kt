package com.twix.designsystem.components.bottomsheet.model

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.DimmedColor

@Immutable
data class CommonBottomSheetConfig(
    val scrimColor: Color = DimmedColor.D070,
    val shape: Shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
    val showHandle: Boolean = true,
    val handleTopPadding: Dp = 11.dp,
    val handleBottomPadding: Dp = 11.dp,
    val maxHeightFraction: Float = 0.8f, // 바텀시트가 최대로 커질 수 있는 높이를 비율로 설정
    val dismissOnScrimClick: Boolean = true,
)
