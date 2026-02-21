package com.twix.designsystem.components.button

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
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.extension.toUiModel
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.LoginType
import com.twix.ui.extension.noRippleClickable

const val LOGIN_BUTTON_HEIGHT = 54

@Composable
fun LoginButton(
    type: LoginType,
    onClickLogin: (LoginType) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiModel = type.toUiModel()

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(LOGIN_BUTTON_HEIGHT.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(uiModel.background)
                .border(1.dp, uiModel.border, RoundedCornerShape(12.dp))
                .noRippleClickable { onClickLogin(uiModel.type) },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(uiModel.logo),
            contentDescription = null,
        )

        Spacer(Modifier.width(12.dp))

        AppText(
            text = stringResource(uiModel.title),
            style = AppTextStyle.T3,
            color = uiModel.textColor,
        )
    }
}

@Preview
@Composable
private fun LoginButtonPreview() {
    TwixTheme {
        LoginButton(
            type = LoginType.GOOGLE,
            onClickLogin = {},
        )
    }
}
