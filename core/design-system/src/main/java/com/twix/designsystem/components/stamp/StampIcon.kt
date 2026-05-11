package com.twix.designsystem.components.stamp

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.extension.toBackgroundRes
import com.twix.designsystem.extension.toBorderRes
import com.twix.designsystem.extension.toResId
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType

@Composable
fun StampIcon(
    stamp: StampType,
    stampColor: StampColor?,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.size(18.dp)) {
        if (stampColor != null) {
            Icon(
                imageVector = ImageVector.vectorResource(stamp.toBackgroundRes()),
                contentDescription = null,
                tint = stampColor.toResId(),
            )
            Icon(
                imageVector = ImageVector.vectorResource(stamp.toBorderRes()),
                contentDescription = null,
                tint = GrayColor.C500,
            )
        } else {
            Icon(
                imageVector = ImageVector.vectorResource(stamp.toBorderRes()),
                contentDescription = null,
                tint = GrayColor.C200,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StampIconPreview() {
    TwixTheme {
        Row {
            StampType.entries.forEach { type ->
                StampIcon(stamp = type, stampColor = StampColor.PINK400)
                StampIcon(stamp = type, stampColor = null)
            }
        }
    }
}
