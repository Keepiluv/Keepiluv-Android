package com.twix.settings.account

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.dialog.CommonDialog
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.topbar.CommonTopBar
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.settings.SettingsIntent
import com.twix.settings.SettingsSideEffect
import com.twix.settings.SettingsViewModel
import com.twix.settings.component.SettingsMenuFrame
import com.twix.settings.component.SettingsMenuItem
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import org.koin.compose.koinInject

@Composable
fun SettingsAccountRoute(
    toastManager: ToastManager = koinInject(),
    viewModel: SettingsViewModel,
    popBackStack: () -> Unit,
    navigateToLogin: () -> Unit,
) {
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    ObserveAsEvents(viewModel.sideEffect) { effect ->
        when (effect) {
            SettingsSideEffect.NavigateToLogin -> navigateToLogin()
            is SettingsSideEffect.ShowToast -> toastManager.show(ToastData(currentContext.getString(effect.resId), effect.type))
        }
    }

    SettingsAccountScreen(
        onBack = popBackStack,
        onLogout = { viewModel.dispatch(SettingsIntent.Logout) },
        onWithdrawAccount = { viewModel.dispatch(SettingsIntent.WithdrawAccount) },
    )
}

@Composable
private fun SettingsAccountScreen(
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onWithdrawAccount: () -> Unit,
) {
    var showWithdrawDialog by remember { mutableStateOf(false) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
    ) {
        CommonTopBar(
            title = stringResource(R.string.word_account),
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
                    .padding(horizontal = 20.dp),
        ) {
            SettingsMenuItem(
                title = stringResource(R.string.word_logout),
                onClick = onLogout,
            )

            HorizontalDivider(thickness = 1.dp, color = GrayColor.C500)

            SettingsMenuItem(
                title = stringResource(R.string.action_withdraw_account),
                onClick = { showWithdrawDialog = true },
            )
        }
    }

    CommonDialog(
        visible = showWithdrawDialog,
        confirmText = stringResource(R.string.word_cancel),
        dismissText = stringResource(R.string.action_withdraw_account),
        onDismiss = {
            showWithdrawDialog = false
            onWithdrawAccount()
        },
        onConfirm = { showWithdrawDialog = false },
        onDismissRequest = { showWithdrawDialog = false },
        content = {
            Image(
                painter = painterResource(R.drawable.ic_warning),
                contentDescription = "warning",
                modifier =
                    Modifier
                        .size(60.dp),
            )

            Spacer(Modifier.height(12.dp))

            AppText(
                text = stringResource(R.string.dialog_withdraw_account_title),
                color = GrayColor.C500,
                style = AppTextStyle.T1,
            )

            Spacer(Modifier.height(8.dp))

            AppText(
                text = stringResource(R.string.dialog_withdraw_account_content),
                color = GrayColor.C400,
                style = AppTextStyle.B2,
                textAlign = TextAlign.Center,
            )
        },
    )
}
