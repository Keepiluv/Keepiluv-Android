package com.twix.onboarding.couple.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
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
                ).noRippleClickable(onClick = onClickConnect),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier =
                Modifier
                    .padding(start = 25.dp),
        ) {
            AppText(
                text = stringResource(R.string.onboarding_couple_connect_direct_description),
                style = AppTextStyle.C1,
                color = GrayColor.C400,
                modifier = Modifier.height(18.dp),
            )
            AppText(
                text = stringResource(R.string.onboarding_couple_direct_connect_button_title),
                style = AppTextStyle.T2,
                color = GrayColor.C500,
                modifier = Modifier.height(24.dp),
            )
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
