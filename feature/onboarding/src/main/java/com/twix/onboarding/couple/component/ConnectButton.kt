package com.twix.onboarding.couple.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight.Companion.ExtraBold
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.NanumSquareNeoFamily
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun ConnectButton(
    onClickConnect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(86.dp)
                .padding(horizontal = 36.dp)
                .background(color = CommonColor.White, shape = RoundedCornerShape(12.dp))
                .border(
                    color = GrayColor.C500,
                    width = 1.2.dp,
                    shape = RoundedCornerShape(12.dp),
                ).noRippleClickable { onClickConnect() },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.padding(start = 25.dp),
            verticalArrangement = Arrangement.Center,
        ) {
            AppText(
                text = stringResource(R.string.onboarding_couple_connect_direct_description),
                style = AppTextStyle.C1,
                color = GrayColor.C400,
            )

            Spacer(modifier = Modifier.height(3.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = stringResource(R.string.onboarding_couple_connect_direct),
                    fontFamily = NanumSquareNeoFamily,
                    fontWeight = ExtraBold,
                    color = GrayColor.C500,
                    fontSize = 16.sp,
                    lineHeight = 22.2.sp,
                )

                AppText(
                    text = stringResource(R.string.onboarding_couple_connect),
                    style = AppTextStyle.T2,
                    color = GrayColor.C500,
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_direct),
            contentDescription = null,
        )

        Spacer(modifier = Modifier.width(25.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun ConnectButtonPreview() {
    TwixTheme {
        ConnectButton(onClickConnect = {})
    }
}
