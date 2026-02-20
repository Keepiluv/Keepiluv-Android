package com.twix.login.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.twix.domain.login.LoginType
import com.twix.domain.model.enums.AppTextStyle
import com.twix.login.model.LoginTypeUiModel
import com.twix.ui.extension.noRippleClickable
import com.twix.designsystem.R as DesR

@Composable
fun LoginButton(
    type: LoginType,
    onClickLogin: (LoginType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiModel = type.uiModel()
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(54.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(uiModel.background)
                .border(1.dp, uiModel.border, RoundedCornerShape(12.dp))
                .noRippleClickable { onClickLogin(type) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = uiModel.logo,
            contentDescription = null,
        )

        Spacer(Modifier.width(12.dp))

        AppText(
            text = uiModel.title,
            style = AppTextStyle.T3,
            color = uiModel.textColor,
        )
    }
}

@Composable
private fun LoginType.uiModel(): LoginTypeUiModel =
    when (this) {
        LoginType.GOOGLE ->
            LoginTypeUiModel(
                logo = ImageVector.vectorResource(DesR.drawable.ic_google),
                title = stringResource(R.string.google_login_button_title),
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
        // TODO : KAKAO용으로 수정
        LoginType.KAKAO ->
            LoginTypeUiModel(
                logo = ImageVector.vectorResource(DesR.drawable.ic_google),
                title = stringResource(R.string.google_login_button_title),
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
    }

@Preview(showBackground = true)
@Composable
fun LoginButtonPreview() {
    TwixTheme {
        LoginButton(
            type = LoginType.GOOGLE,
            onClickLogin = {},
        )
    }
}
