package com.twix.home.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppRoundButton
import com.twix.designsystem.components.goal.GoalVerificationCell
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.goal.GoalVerification
import com.twix.ui.extension.noRippleClickable

@Composable
fun GoalVerifications(
    modifier: Modifier = Modifier,
    myVerification: GoalVerification?,
    partnerVerification: GoalVerification?,
    onMyClick: (() -> Unit)? = null,
    onPartnerClick: (() -> Unit)? = null,
    onPokeGoal: () -> Unit,
) {
    val shape = RoundedCornerShape(bottomEnd = 16.dp, bottomStart = 16.dp)

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .border(1.dp, GrayColor.C500, shape),
    ) {
        GoalVerificationCell(
            modifier = Modifier.weight(1f),
            verification = myVerification,
            emptyContent = {
                EmptyContent(
                    onClick = {},
                )
            },
            onClick = onMyClick,
        )

        VerticalDivider(thickness = 1.dp, color = GrayColor.C500)

        GoalVerificationCell(
            modifier = Modifier.weight(1f),
            verification = partnerVerification,
            emptyContent = {
                EmptyContent(
                    isPartner = true,
                    onClick = onPokeGoal,
                )
            },
            onClick = onPartnerClick,
        )
    }
}

@Composable
private fun EmptyContent(
    isPartner: Boolean = false,
    onClick: () -> Unit,
) {
    if (isPartner) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_goal_action_poke),
                contentDescription = null,
                modifier = Modifier.size(width = 85.dp, height = 53.dp),
            )

            AppRoundButton(
                text = stringResource(R.string.action_sting_emphasized),
                textColor = GrayColor.C500,
                textStyle = AppTextStyle.C2,
                backgroundColor = CommonColor.White,
                modifier =
                    Modifier
                        .size(width = 64.dp, height = 28.dp)
                        .noRippleClickable { onClick() },
            )
        }
    } else {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(R.drawable.ic_goal_action_cheer),
                contentDescription = null,
                modifier = Modifier.size(width = 56.dp, height = 64.dp),
            )

            AppText(
                text = stringResource(R.string.goal_action_cheer),
                style = AppTextStyle.B4,
                color = GrayColor.C400,
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun PreviewEmptyContent() {
    TwixTheme {
        EmptyContent(
            isPartner = true,
            onClick = {},
        )
    }
}
