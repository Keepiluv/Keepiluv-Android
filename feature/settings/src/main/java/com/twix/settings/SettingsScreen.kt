package com.twix.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.text_field.ValidateUnderlineTextField
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.component.SettingsMenuItem
import com.twix.settings.model.SettingsUiState
import com.twix.ui.extension.dismissKeyboardOnTap
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
        onCommitNickName = { viewModel.dispatch(SettingsIntent.SetNickName(it)) },
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState = SettingsUiState(),
    onBack: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onCommitNickName: (String) -> Unit = {},
) {
    var isEditMode by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .dismissKeyboardOnTap(onDismiss = { isEditMode = false })
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
            ProfileInfo(
                nickname = uiState.nickName,
                isEditMode = isEditMode,
                onCommitNickName = {
                    onCommitNickName(it)
                    isEditMode = false
                },
                onEditModeChange = { isEditMode = it },
            )

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
private fun ProfileInfo(
    nickname: String,
    isEditMode: Boolean,
    onCommitNickName: (String) -> Unit,
    onEditModeChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Image(
            painter = painterResource(R.drawable.ic_profile),
            contentDescription = "profile",
            modifier =
                Modifier
                    .size(52.dp),
        )

        Column(
            horizontalAlignment = Alignment.Start,
        ) {
            if (isEditMode) {
                ValidateUnderlineTextField(
                    modifier =
                        Modifier
                            .height(77.dp),
                    value = nickname,
                    onCommit = onCommitNickName,
                    placeholder = stringResource(R.string.settings_nickname_placeholder),
                    guideText = stringResource(R.string.settings_nickname_text_filed_guide),
                    validLengthRange = 2..8,
                )
            } else {
                Row(
                    modifier =
                        Modifier
                            .height(52.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                ) {
                    Spacer(Modifier.width(16.dp))

                    AppText(
                        text = nickname,
                        style = AppTextStyle.T1,
                        color = GrayColor.C500,
                    )

                    Image(
                        painter = painterResource(R.drawable.ic_edit),
                        contentDescription = null,
                        modifier =
                            Modifier
                                .padding(10.dp)
                                .size(24.dp)
                                .noRippleClickable { onEditModeChange(true) },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        SettingsScreen()
    }
}
