package com.twix.designsystem.components.photolog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme

@Composable
fun PhotologCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GrayColor.C500,
    background: Color = CommonColor.White,
    rotation: Float = 0f,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier =
            modifier
                .padding(horizontal = 27.dp)
                .fillMaxWidth()
                .aspectRatio(1f)
                .rotate(rotation)
                .clip(shape = RoundedCornerShape(12.dp))
                .border(
                    width = 1.6.dp,
                    color = borderColor,
                    shape = RoundedCornerShape(12.dp),
                ).background(background),
        contentAlignment = Alignment.Center,
        content = content,
    )
}

@Preview
@Composable
fun PhotologCardPreview() {
    TwixTheme {
        PhotologCard(
            rotation = 0f,
            background = Color.White,
            borderColor = Color.Black,
        )
    }
}
