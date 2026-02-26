package com.twix.designsystem.components.dialog

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable

@Composable
fun MarketingDialog(
    visible: Boolean,
    onConfirm: (Boolean, Boolean) -> Unit,
) {
    var isMarketingChecked by remember { mutableStateOf(true) }
    var isNightMarketingChecked by remember { mutableStateOf(true) }

    CommonDialog(
        visible = visible,
        confirmText = stringResource(R.string.word_confirm),
        dismissText = null,
        onDismissRequest = { },
        onConfirm = {
            onConfirm(isMarketingChecked, isNightMarketingChecked)
        },
        content = {
            MarketingDialogContent(
                isMarketingChecked = isMarketingChecked,
                isNightMarketingChecked = isNightMarketingChecked,
                onMarketingToggle = {
                    isMarketingChecked = !isMarketingChecked
                },
                onNightMarketingToggle = {
                    isNightMarketingChecked = !isNightMarketingChecked
                },
            )
        },
    )
}

@Composable
private fun MarketingDialogContent(
    isMarketingChecked: Boolean,
    isNightMarketingChecked: Boolean,
    onMarketingToggle: () -> Unit,
    onNightMarketingToggle: () -> Unit,
) {
    Column {
        AppText(
            text = stringResource(R.string.marketing_dialog_title),
            style = AppTextStyle.T1,
            color = GrayColor.C500,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(24.dp))

        MarketingCheckItem(
            text = stringResource(R.string.marketing_dialog_marketing),
            checked = isMarketingChecked,
            onClick = onMarketingToggle,
        )

        Spacer(Modifier.height(12.dp))

        MarketingCheckItem(
            text = stringResource(R.string.marketing_dialog_night_marketing),
            checked = isNightMarketingChecked,
            onClick = onNightMarketingToggle,
        )

        Spacer(Modifier.height(14.dp))

        AppText(
            text = stringResource(R.string.marketing_dialog_description),
            style = AppTextStyle.C2,
            color = GrayColor.C300,
            modifier = Modifier.padding(start = 10.dp),
        )
    }
}

@Composable
private fun MarketingCheckItem(
    text: String,
    checked: Boolean,
    onClick: () -> Unit,
) {
    val icon = if (checked) R.drawable.ic_checked_you else R.drawable.ic_empty_check

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Image(
            painter = painterResource(icon),
            contentDescription = null,
            modifier =
                Modifier
                    .size(24.dp)
                    .noRippleClickable(onClick = onClick),
        )

        Spacer(Modifier.width(8.dp))

        AppText(
            text = text,
            style = AppTextStyle.B2,
            color = GrayColor.C500,
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MarketingDialogPreview() {
    TwixTheme {
        MarketingDialog(
            visible = true,
            onConfirm = { _, _ -> },
        )
    }
}
