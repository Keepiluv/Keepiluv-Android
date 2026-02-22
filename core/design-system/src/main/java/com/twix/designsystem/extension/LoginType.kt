package com.twix.designsystem.extension

import com.twix.designsystem.R
import com.twix.designsystem.components.button.model.LoginTypeUiModel
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.LoginType

fun LoginType.toUiModel(): LoginTypeUiModel =
    when (this) {
        LoginType.GOOGLE ->
            LoginTypeUiModel(
                type = this,
                logo = R.drawable.ic_google,
                title = R.string.google_login_button_title,
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
        // TODO : KAKAO용으로 수정
        LoginType.KAKAO ->
            LoginTypeUiModel(
                type = this,
                logo = R.drawable.ic_google,
                title = R.string.google_login_button_title,
                background = CommonColor.White,
                border = GrayColor.C200,
                textColor = GrayColor.C500,
            )
    }
