package com.twix.designsystem.components.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme

@Composable
fun TwixLoadingOverlay(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        TwixLoadingIndicator()
    }
}

@Composable
private fun TwixLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = 24.dp,
    strokeWidth: Dp = 2.5.dp,
) {
    CircularProgressIndicator(
        modifier = modifier.size(size),
        strokeWidth = strokeWidth,
        color = GrayColor.C500,
        trackColor = GrayColor.C100,
    )
}

@Preview(showBackground = true)
@Composable
private fun TwixLoadingOverlayPreview() {
    TwixTheme {
        TwixLoadingOverlay()
    }
}
