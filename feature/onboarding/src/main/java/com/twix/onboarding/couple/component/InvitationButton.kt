package com.twix.onboarding.couple.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import com.twix.designsystem.R as DesR

@Composable
internal fun InvitationButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(86.dp)
                .padding(horizontal = 36.dp)
                .background(color = GrayColor.C500, shape = RoundedCornerShape(12.dp))
                .noRippleClickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        AppText(
            text = stringResource(DesR.string.onboarding_couple_connect_send_invitation),
            style = AppTextStyle.T2,
            color = CommonColor.White,
        )
    }
}

@Preview
@Composable
private fun InvitationButtonPreview() {
    TwixTheme {
        InvitationButton(
            onClick = {},
        )
    }
}
