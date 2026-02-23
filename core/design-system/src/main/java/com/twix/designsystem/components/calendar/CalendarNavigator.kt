package com.twix.designsystem.components.calendar

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
fun CalendarNavigator(
    currentDate: LocalDate,
    onNextMonth: () -> Unit,
    onPreviousMonth: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .padding(vertical = 4.5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Image(
            painter = painterResource(R.drawable.ic_arrow_m_left),
            contentDescription = "previous month",
            modifier =
                Modifier
                    .padding(6.dp)
                    .size(24.dp)
                    .noRippleClickable(onClick = onPreviousMonth),
        )

        AppText(
            text = "%d.%02d".format(currentDate.year, currentDate.monthValue),
            style = AppTextStyle.T1,
            color = GrayColor.C500,
            modifier = Modifier.width(84.dp),
        )

        Image(
            painter = painterResource(R.drawable.ic_arrow_m_right),
            contentDescription = "next month",
            modifier =
                Modifier
                    .padding(6.dp)
                    .size(24.dp)
                    .noRippleClickable(onClick = onNextMonth),
        )
    }
}

@Preview
@Composable
fun CalendarNavigatorPreview() {
    TwixTheme {
        CalendarNavigator(
            currentDate = LocalDate.now(),
            onNextMonth = {},
            onPreviousMonth = {},
        )
    }
}
