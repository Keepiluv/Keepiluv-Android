package com.twix.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.component.SettingsMenuItem
import com.twix.settings.model.SettingsUiState
import com.twix.ui.extension.noRippleClickable
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    popBackStack: () -> Unit,
    navigateToSettingsAccount: () -> Unit,
    navigateToSettingsAbout: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onBack = popBackStack,
        onAccountClick = navigateToSettingsAccount,
        onAboutClick = navigateToSettingsAbout,
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState,
    onBack: () -> Unit,
    onAccountClick: () -> Unit,
    onAboutClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
    ) {
        CommonTopBar(
            title = stringResource(R.string.word_setting),
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

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
        ) {
            ProfileInfo(nickname = uiState.nickName)

            Spacer(Modifier.height(24.dp))

            SettingsMenuFrame {
                SettingsMenuItem(
                    resId = R.drawable.ic_profile_small,
                    title = stringResource(R.string.word_account),
                    onClick = onAccountClick,
                )

                HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

                SettingsMenuItem(
                    resId = R.drawable.ic_info,
                    title = stringResource(R.string.word_information),
                    onClick = onAboutClick,
                )
            }
        }
    }
}

@Composable
private fun ProfileInfo(nickname: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "profile",
            modifier =
                Modifier
                    .size(52.dp),
        )

        Spacer(Modifier.width(16.dp))

        AppText(
            text = nickname,
            style = AppTextStyle.T1,
            color = GrayColor.C500,
        )
    }
}
