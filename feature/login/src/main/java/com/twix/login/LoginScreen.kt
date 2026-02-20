package com.twix.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.LoginButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.login.LoginType
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.enums.AppTextStyle
import com.twix.login.contract.LoginIntent
import com.twix.login.contract.LoginSideEffect
import com.twix.ui.base.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginRoute(
    navigateToHome: () -> Unit,
    navigateToOnBoarding: (OnboardingStatus) -> Unit,
    toastManager: ToastManager = koinInject(),
    loginProvider: LoginProviderFactory = koinInject(),
    viewModel: LoginViewModel = koinViewModel(),
) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            LoginSideEffect.NavigateToHome -> navigateToHome()
            is LoginSideEffect.NavigateToOnBoarding -> navigateToOnBoarding(sideEffect.status)
            is LoginSideEffect.ShowToast ->
                toastManager
                    .tryShow(
                        ToastData(
                            currentContext.getString(sideEffect.message),
                            sideEffect.type,
                        ),
                    )
        }
    }

    LoginScreen { type ->
        coroutineScope.launch {
            viewModel.dispatch(LoginIntent.Login(loginProvider[type].login()))
        }
    }
}

@Composable
private fun LoginScreen(onClickLogin: (LoginType) -> Unit) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(CommonColor.White),
        ) {
            Spacer(Modifier.height(35.dp))

            Image(
                imageVector = ImageVector.vectorResource(com.twix.designsystem.R.drawable.ic_app_logo),
                contentDescription = null,
                modifier =
                    Modifier
                        .padding(start = 24.dp),
            )

            Spacer(Modifier.height(24.dp))

            AppText(
                text = stringResource(R.string.login_title_message),
                style = AppTextStyle.H3,
                color = GrayColor.C500,
                modifier =
                    Modifier
                        .padding(start = 24.dp),
            )

            Spacer(Modifier.height(27.dp))

            Image(
                imageVector = ImageVector.vectorResource(com.twix.designsystem.R.drawable.ic_keepi_singing),
                contentDescription = null,
            )

            LoginType.entries.forEach { type ->
                LoginButton(
                    type = type,
                    onClickLogin = onClickLogin,
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp),
                )
            }

            Spacer(Modifier.height(27.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    TwixTheme {
        LoginScreen(onClickLogin = {})
    }
}
