package com.twix.settings

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.component.ProfileInfo
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.component.SettingsMenuItem
import com.twix.settings.model.SettingsLanguage
import com.twix.settings.model.SettingsUiState
import com.twix.ui.extension.dismissKeyboardOnTap
import com.twix.ui.extension.noRippleClickable
import com.twix.util.extension.openExternalUrl
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun SettingsRoute(
    viewModel: SettingsViewModel = koinViewModel(),
    toastManager: ToastManager = koinInject(),
    popBackStack: () -> Unit,
    navigateToSettingsAccount: () -> Unit,
    navigateToSettingsAbout: () -> Unit,
    navigateToSettingsNotification: () -> Unit,
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SettingsScreen(
        uiState = uiState,
        onBack = popBackStack,
        onAccountClick = navigateToSettingsAccount,
        onAboutClick = navigateToSettingsAbout,
        onInquiryClick = {
            currentContext.openExternalUrl(
                url = BuildConfig.KAKAO_OPEN_CHAT_URL,
                onFailed = {
                    toastManager.tryShow(
                        ToastData(
                            message = currentContext.getString(R.string.settings_inquiry_open_failed),
                            type = ToastType.ERROR,
                        ),
                    )
                },
            )
        },
        onNotificationClick = navigateToSettingsNotification,
        onCommitNickName = { viewModel.dispatch(SettingsIntent.SetNickName(it)) },
    )
}

@Composable
private fun SettingsScreen(
    uiState: SettingsUiState = SettingsUiState(),
    onBack: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onAboutClick: () -> Unit = {},
    onInquiryClick: () -> Unit = {},
    onNotificationClick: () -> Unit = {},
    onCommitNickName: (String) -> Unit = {},
) {
    var isEditMode by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .dismissKeyboardOnTap(onDismiss = { isEditMode = false })
                .background(CommonColor.White),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                        resId = R.drawable.ic_language,
                        title = stringResource(R.string.settings_language),
                        right = {
                            LanguageMenuRight(language = SettingsLanguage.Korean)
                        },
                        onClick = { showLanguageDialog = true },
                    )

                    SettingsDivider()

                    SettingsMenuItem(
                        resId = R.drawable.ic_profile_small,
                        title = stringResource(R.string.word_account),
                        onClick = onAccountClick,
                    )

                    SettingsDivider()

                    SettingsMenuItem(
                        resId = R.drawable.ic_info,
                        title = stringResource(R.string.word_information),
                        onClick = onAboutClick,
                    )

                    SettingsDivider()

                    SettingsMenuItem(
                        resId = R.drawable.ic_question,
                        title = stringResource(R.string.settings_inquiry),
                        right = {
                            AppText(
                                text = stringResource(R.string.settings_inquiry_time),
                                style = AppTextStyle.B2,
                                color = GrayColor.C500,
                            )
                        },
                        onClick = onInquiryClick,
                    )

                    SettingsDivider()

                    SettingsMenuItem(
                        resId = R.drawable.ic_notification,
                        title = stringResource(R.string.settings_notification),
                        onClick = onNotificationClick,
                    )
                }
            }
        }

        LanguageSettingDialog(
            visible = showLanguageDialog,
            selectedLanguage = SettingsLanguage.Korean,
            onDismissRequest = {
                showLanguageDialog = false
            },
            onConfirm = {
                showLanguageDialog = false
            },
            onDismiss = {
                showLanguageDialog = false
            },
        )
    }
}

@Composable
private fun LanguageSettingDialog(
    visible: Boolean,
    selectedLanguage: SettingsLanguage,
    onDismissRequest: () -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    CommonDialog(
        visible = visible,
        confirmText = stringResource(R.string.word_completion),
        dismissText = stringResource(R.string.word_cancel),
        onDismissRequest = onDismissRequest,
        onConfirm = onConfirm,
        onDismiss = onDismiss,
        content = {
            AppText(
                text = stringResource(R.string.settings_language),
                style = AppTextStyle.T1,
                color = GrayColor.C500,
            )

            Spacer(Modifier.height(8.dp))

            AppText(
                text = stringResource(R.string.settings_language_description),
                style = AppTextStyle.B2,
                color = GrayColor.C400,
            )

            Spacer(Modifier.height(24.dp))

            LanguageDialogItem(
                language = SettingsLanguage.Korean,
                selected = selectedLanguage == SettingsLanguage.Korean,
            )
        },
    )
}

@Composable
private fun LanguageDialogItem(
    language: SettingsLanguage,
    selected: Boolean,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter =
                painterResource(
                    if (selected) {
                        R.drawable.ic_checked_you
                    } else {
                        R.drawable.ic_empty_check
                    },
                ),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )

        Spacer(Modifier.width(8.dp))

        AppText(
            text = language.displayName,
            style = AppTextStyle.B2,
            color = GrayColor.C500,
        )
    }
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(
        thickness = 1.dp,
        color = GrayColor.C500,
    )
}

@Composable
private fun LanguageMenuRight(language: SettingsLanguage = SettingsLanguage.Korean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AppText(
            text = language.displayName,
            style = AppTextStyle.B2,
            color = GrayColor.C500,
        )

        Spacer(Modifier.width(8.dp))

        Image(
            painter = painterResource(R.drawable.ic_arrow_down_circle),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun Preview() {
    TwixTheme {
        SettingsScreen()
    }
}
