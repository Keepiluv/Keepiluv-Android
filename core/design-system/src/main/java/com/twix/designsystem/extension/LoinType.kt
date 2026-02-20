package com.twix.designsystem.extension

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import com.twix.designsystem.R
import com.twix.designsystem.components.button.model.LoginTypeUiModel
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.login.LoginType

@Composable
internal fun LoginType.toUiModel(): LoginTypeUiModel =
    when (this) {
        LoginType.GOOGLE ->
            LoginTypeUiModel(
                logo = ImageVector.vectorResource(R.drawable.ic_google),
                title = stringResource(R.string.google_login_button_title),
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
        // TODO : KAKAO용으로 수정
        LoginType.KAKAO ->
            LoginTypeUiModel(
                logo = ImageVector.vectorResource(R.drawable.ic_google),
                title = stringResource(R.string.google_login_button_title),
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
    }
