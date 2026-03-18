package com.twix.onboarding.dday.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.onboarding.dday.DdayUiModel
import com.twix.ui.extension.noRippleClickable
import java.time.LocalDate

@Composable
internal fun DDayField(
    uiModel: DdayUiModel,
    onDateClick: () -> Unit,
) {
    Column(
        Modifier
            .fillMaxWidth()
            .height(52.dp)
            .padding(horizontal = 20.dp),
    ) {
        Row(
            modifier = Modifier.height(44.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppText(
                text =
                    if (uiModel.isSelected) {
                        uiModel.anniversaryDate.toString()
                    } else {
                        stringResource(
                            R.string.onboarding_dday_placeholder,
                        )
                    },
                style = AppTextStyle.T2,
                color = if (uiModel.isSelected) GrayColor.C500 else GrayColor.C200,
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(start = 8.dp),
            )

            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_calendar),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(end = 10.dp)
                        .noRippleClickable(onClick = onDateClick),
            )
        }
        Spacer(Modifier.height(4.dp))

        HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)
    }
}

@Preview(showBackground = true)
@Composable
private fun DDayFieldPreview() {
    TwixTheme {
        Column {
            DDayField(
                uiModel =
                    DdayUiModel(
                        anniversaryDate = LocalDate.now(),
                        isSelected = false,
                    ),
                onDateClick = {},
            )
            DDayField(
                uiModel =
                    DdayUiModel(
                        anniversaryDate = LocalDate.of(2024, 1, 1),
                        isSelected = true,
                    ),
                onDateClick = {},
            )
        }
    }
}
