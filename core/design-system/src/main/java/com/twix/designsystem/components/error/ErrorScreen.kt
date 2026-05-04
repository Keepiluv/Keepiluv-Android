package com.twix.designsystem.components.error

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
fun ErrorScreen(
    onClickBack: () -> Unit,
    onClickRetry: () -> Unit,
    modifier: Modifier = Modifier,
    showBackButton: Boolean = true,
    title: String = stringResource(R.string.error_load_failed_title),
    message: String = stringResource(R.string.error_load_failed_message),
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 89.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_empty_trash),
            contentDescription = "error icon",
        )

        Spacer(Modifier.height(16.dp))

        AppText(
            text = title,
            style = AppTextStyle.T2,
            color = GrayColor.C400,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(4.dp))

        AppText(
            text = message,
            style = AppTextStyle.C1,
            color = GrayColor.C300,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(20.dp))

        ActionButtons(
            showBackButton = showBackButton,
            onClickBack = onClickBack,
            onClickRetry = onClickRetry,
        )
    }
}

@Composable
private fun ActionButtons(
    showBackButton: Boolean,
    onClickBack: () -> Unit,
    onClickRetry: () -> Unit,
) {
    Row {
        if (showBackButton) {
            AppButton(
                text = stringResource(R.string.error_back_button_label),
                textColor = CommonColor.White,
                textStyle = AppTextStyle.B1,
                backgroundColor = GrayColor.C500,
                cornerRadius = 999.dp,
                onClick = onClickBack,
                modifier = Modifier.weight(1f),
            )

            Spacer(modifier = Modifier.width(8.dp))
        }

        AppButton(
            text = stringResource(R.string.error_retry_button_label),
            textColor = if (showBackButton) GrayColor.C500 else CommonColor.White,
            textStyle = AppTextStyle.B1,
            backgroundColor = if (showBackButton) CommonColor.White else GrayColor.C500,
            border =
                if (showBackButton) {
                    BorderStroke(
                        width = 1.dp,
                        color = GrayColor.C500,
                    )
                } else {
                    null
                },
            cornerRadius = 999.dp,
            onClick = onClickRetry,
            modifier = if (showBackButton) Modifier.weight(1f) else Modifier,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ErrorScreenPreview() {
    TwixTheme {
        ErrorScreen(
            onClickBack = {},
            onClickRetry = {},
        )
    }
}
