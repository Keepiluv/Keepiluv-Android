package com.twix.onboarding.couple.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
internal fun RestoreCoupleBottomSheetContent() {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
                .padding(horizontal = 30.dp),
    ) {
        AppText(
            text = stringResource(R.string.onboarding_couple_restore),
            style = AppTextStyle.T1,
            color = GrayColor.C500,
        )

        Spacer(Modifier.height(3.dp))

        AppText(
            text = stringResource(R.string.onboarding_couple_restore_bottom_sheet_content),
            style = AppTextStyle.B2,
            color = GrayColor.C400,
        )

        Spacer(Modifier.height(18.dp))

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(78.dp)
                    .background(GrayColor.C050, RoundedCornerShape(12.dp)),
            verticalArrangement = Arrangement.Center,
        ) {
            BulletItem(stringResource(R.string.onboarding_couple_restore_content_my_email))
            BulletItem(stringResource(R.string.onboarding_couple_restore_content_partner_email))
            BulletItem(stringResource(R.string.onboarding_couple_restore_content_restore_date))
        }
    }
}

@Composable
private fun BulletItem(text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .padding(start = 16.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .size(6.dp)
                    .background(GrayColor.C300, CircleShape),
        )

        Spacer(Modifier.width(8.dp))

        AppText(
            text = text,
            style = AppTextStyle.B4,
            color = GrayColor.C300,
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RestoreCoupleBottomSheetContentPreview() {
    TwixTheme {
        RestoreCoupleBottomSheetContent()
    }
}
