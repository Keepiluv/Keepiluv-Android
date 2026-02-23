package com.twix.designsystem.components.stamp

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType

@Composable
fun StampGrid(
    stamp: StampType,
    stampColors: List<StampColor>,
    monthlyTargetCount: Int,
    modifier: Modifier = Modifier,
) {
    val maxItemsInEachRow = 7
    val stampIndices = (0 until monthlyTargetCount).chunked(maxItemsInEachRow)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        stampIndices.forEach { rowIndices ->
            Row(
                modifier = modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                rowIndices.forEach { index ->
                    StampIcon(stamp = stamp, stampColor = stampColors.getOrNull(index))
                }

                if (rowIndices.size < maxItemsInEachRow) {
                    repeat(maxItemsInEachRow - rowIndices.size) {
                        Spacer(modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StampGridPreview() {
    TwixTheme {
        StampGrid(
            stamp = StampType.HEART,
            stampColors = listOf(StampColor.PINK400),
            monthlyTargetCount = 10,
        )
    }
}
