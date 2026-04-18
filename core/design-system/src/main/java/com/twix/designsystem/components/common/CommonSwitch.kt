package com.twix.designsystem.components.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import kotlin.math.roundToInt

@Composable
fun CommonSwitch(
    modifier: Modifier = Modifier,
    checked: Boolean,
    onClick: (Boolean) -> Unit,
) {
    val density = LocalDensity.current
    val minBound = with(density) { 0.dp.toPx() }
    val maxBound = with(density) { 18.dp.toPx() }
    val state by animateFloatAsState(
        targetValue = if (checked) maxBound else minBound,
        animationSpec = tween(durationMillis = 500),
        label = "common switch",
    )

    Box(
        modifier =
            modifier
                .size(width = 48.dp, height = 30.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(if (checked) GrayColor.C500 else CommonColor.White)
                .border(1.dp, GrayColor.C500, RoundedCornerShape(999.dp))
                .clickable(onClick = { onClick(!checked) }),
        contentAlignment = Alignment.CenterStart,
    ) {
        Box(
            modifier =
                Modifier
                    .offset { IntOffset(state.roundToInt(), 0) }
                    .padding(4.dp)
                    .size(22.dp)
                    .clip(CircleShape)
                    .background(if (checked) CommonColor.White else GrayColor.C500),
        )
    }
}
