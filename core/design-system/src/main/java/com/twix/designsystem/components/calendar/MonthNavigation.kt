package com.twix.designsystem.components.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun MonthNavigation(
    date: String,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(space = 12.dp, alignment = Alignment.CenterHorizontally),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_m_left),
            contentDescription = null,
            modifier = Modifier.noRippleClickable { onPreviousMonthClick() },
        )

        AppText(
            text = date,
            style = AppTextStyle.T1,
            color = GrayColor.C500,
        )

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_arrow_m_right),
            contentDescription = null,
            modifier = Modifier.noRippleClickable { onNextMonthClick() },
        )
    }
}

@Preview
@Composable
fun MonthNavigationPreview() {
    TwixTheme {
        MonthNavigation(
            date = "2024.6",
            onPreviousMonthClick = {},
            onNextMonthClick = {},
        )
    }
}
