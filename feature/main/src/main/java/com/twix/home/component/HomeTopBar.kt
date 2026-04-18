package com.twix.home.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun HomeTopBar(
    monthYearText: String,
    onNotificationClick: () -> Unit,
    onSettingClick: () -> Unit,
    onMoveToToday: () -> Unit,
    onShowCalendarBottomSheet: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 10.dp)
                .padding(bottom = 10.dp),
        verticalAlignment = Alignment.Bottom,
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            Spacer(Modifier.height(12.dp))

            Row(
                modifier =
                    Modifier
                        .noRippleClickable(onClick = onShowCalendarBottomSheet),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AppText(
                    text = monthYearText,
                    style = AppTextStyle.T3,
                    color = GrayColor.C400,
                )

                Image(
                    painter = painterResource(R.drawable.ic_drop_down_arrow),
                    contentDescription = "drop down arrow",
                    modifier = Modifier.size(20.dp),
                    colorFilter = ColorFilter.tint(GrayColor.C400),
                )
            }

            Spacer(Modifier.height(4.dp))

            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                AppText(
                    text = stringResource(R.string.home_today_goal),
                    style = AppTextStyle.H3Brand,
                    color = GrayColor.C400,
                )

                Image(
                    painter = painterResource(R.drawable.ic_refresh),
                    contentDescription = "refresh",
                    modifier = Modifier.noRippleClickable(onClick = onMoveToToday),
                )
            }

            Spacer(Modifier.height(2.dp))
        }

        Spacer(Modifier.weight(1f))

        TopBarButton(
            iconRes = R.drawable.ic_alert,
            contentDescription = "notification",
            onClick = onNotificationClick,
        )

        TopBarButton(
            iconRes = R.drawable.ic_setting,
            contentDescription = "setting",
            onClick = onSettingClick,
        )
    }
}

@Composable
private fun TopBarButton(
    @DrawableRes iconRes: Int,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier =
            Modifier
                .size(40.dp)
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    TwixTheme {
        HomeTopBar(
            monthYearText = "1월 22일",
            {},
            {},
            {},
            {},
        )
    }
}
