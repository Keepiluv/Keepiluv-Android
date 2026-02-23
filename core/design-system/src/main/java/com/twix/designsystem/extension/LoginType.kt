package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.designsystem.components.button.model.LoginTypeUiModel
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.LoginColor
import com.twix.domain.model.enums.LoginType

fun LoginType.toUiModel(): LoginTypeUiModel =
    when (this) {
        LoginType.GOOGLE ->
            LoginTypeUiModel(
                type = this,
                logo = R.drawable.ic_google,
                title = R.string.google_login_button_title,
                background = LoginColor.Google,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
        LoginType.KAKAO ->
            LoginTypeUiModel(
                type = this,
                logo = R.drawable.ic_kakao,
                title = R.string.kakao_login_button_title,
                background = LoginColor.Kakao,
                border = LoginColor.Kakao,
                textColor = GrayColor.C500,
            )
    }
