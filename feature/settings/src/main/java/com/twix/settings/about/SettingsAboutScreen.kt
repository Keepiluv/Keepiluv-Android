package com.twix.settings.about

import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.BuildConfig
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.component.SettingsMenuItem
import com.twix.ui.extension.getAppVersion
import com.twix.ui.extension.noRippleClickable

@Composable
fun SettingsAboutRoute(popBackStack: () -> Unit) {
    val context = LocalContext.current

    SettingsAboutScreen(
        onBack = popBackStack,
        onPrivacyPolicyClick = {
            val url = BuildConfig.PRIVACY_POLICY_URL
            val intent =
                CustomTabsIntent
                    .Builder()
                    .build()
            intent.launchUrl(context, url.toUri())
        },
    )
}

@Composable
private fun SettingsAboutScreen(
    onBack: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        CommonTopBar(
            title = stringResource(R.string.word_information),
            left = {
                Image(
                    painter = painterResource(R.drawable.ic_arrow3_left),
                    contentDescription = "back",
                    modifier =
                        Modifier
                            .padding(18.dp)
                            .size(24.dp)
                            .noRippleClickable(onClick = onBack),
                )
            },
        )

        Spacer(Modifier.height(20.dp))

        SettingsMenuFrame(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
            content = {
                SettingsMenuItem(
                    title = stringResource(R.string.settings_privacy_policy),
                    onClick = onPrivacyPolicyClick,
                )

                HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

                SettingsMenuItem(
                    title = stringResource(R.string.settings_my_app_version),
                    right = {
                        AppText(
                            text = context.getAppVersion(),
                            style = AppTextStyle.B2,
                            color = GrayColor.C500,
                        )
                    },
                )
            },
        )
    }
}
