package com.twix.settings.notification

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.common.CommonSwitch
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.SettingsIntent
import com.twix.settings.SettingsViewModel
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.model.SettingsUiState
import com.twix.ui.extension.noRippleClickable

@Composable
fun SettingsNotificationRoute(
    viewModel: SettingsViewModel,
    popBackStack: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsNotificationScreen(
        uiState = uiState,
        onBack = popBackStack,
        onPokeNotificationChange = {
            viewModel.dispatch(SettingsIntent.SetPokeNotificationEnabled(it))
        },
        onMarketingNotificationChange = {
            viewModel.dispatch(SettingsIntent.SetMarketingNotificationEnabled(it))
        },
        onNightMarketingNotificationChange = {
            viewModel.dispatch(SettingsIntent.SetNightMarketingNotificationEnabled(it))
        },
    )
}

@Composable
private fun SettingsNotificationScreen(
    uiState: SettingsUiState = SettingsUiState(),
    onBack: () -> Unit = {},
    onPokeNotificationChange: (Boolean) -> Unit = {},
    onMarketingNotificationChange: (Boolean) -> Unit = {},
    onNightMarketingNotificationChange: (Boolean) -> Unit = {},
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
    ) {
        CommonTopBar(
            title = stringResource(R.string.settings_notification),
            left = {
                Image(
                    painter = painterResource(R.drawable.ic_arrow3_left),
                    contentDescription = "back",
                    modifier =
                        Modifier
                            .padding(18.dp)
                            .noRippleClickable(onClick = onBack),
                )
            },
        )

        Spacer(Modifier.height(20.dp))

        SettingsMenuFrame(
            modifier = Modifier.padding(horizontal = 20.dp),
        ) {
            NotificationSettingItem(
                title = stringResource(R.string.settings_poke_push_notification),
                checked = uiState.pokeNotificationEnabled,
                onCheckedChange = onPokeNotificationChange,
            )

            SettingsNotificationDivider()

            NotificationSettingItem(
                title = stringResource(R.string.settings_marketing_push_notification),
                checked = uiState.marketingNotificationEnabled,
                onCheckedChange = onMarketingNotificationChange,
            )

            SettingsNotificationDivider()

            NotificationSettingItem(
                title = stringResource(R.string.settings_night_marketing_push_notification),
                checked = uiState.nightMarketingNotificationEnabled,
                onCheckedChange = onNightMarketingNotificationChange,
            )
        }
    }
}

@Composable
private fun NotificationSettingItem(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(60.dp)
                .padding(start = 16.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(
            text = title,
            style = AppTextStyle.B1,
            color = GrayColor.C500,
        )

        Spacer(Modifier.weight(1f))

        CommonSwitch(
            checked = checked,
            onClick = onCheckedChange,
        )
    }
}

@Composable
private fun SettingsNotificationDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = GrayColor.C500,
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun SettingsNotificationScreenPreview() {
    TwixTheme {
        SettingsNotificationScreen()
    }
}
