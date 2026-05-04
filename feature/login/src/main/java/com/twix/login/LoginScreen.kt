package com.twix.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.error.ErrorScreen
import com.twix.designsystem.components.loading.TwixLoadingOverlay
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.LoginType
import com.twix.login.component.LoginButton
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
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val hasException by viewModel.hasException.collectAsStateWithLifecycle()

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

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> TwixLoadingOverlay()
            hasException ->
                ErrorScreen(
                    onClickRetry = { viewModel.clearException(showException = true) },
                    showBackButton = false,
                )
            else ->
                LoginScreen { type ->
                    coroutineScope.launch {
                        viewModel.dispatch(LoginIntent.Login(loginProvider[type].login()))
                    }
                }
        }
    }
}

@Composable
private fun LoginScreen(onClickLogin: (LoginType) -> Unit) {
    val scrollState = rememberScrollState()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White)
                .verticalScroll(scrollState),
    ) {
        Spacer(Modifier.height(35.dp))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_app_logo),
            contentDescription = null,
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(Modifier.height(24.dp))

        AppText(
            text = stringResource(R.string.login_title_message),
            style = AppTextStyle.H3,
            color = GrayColor.C500,
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(Modifier.height(27.dp))

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_singing),
            contentDescription = null,
        )

        Column(
            modifier =
                Modifier
                    .padding(horizontal = 20.dp)
                    .padding(top = 29.dp, bottom = 27.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            LoginType.entries.forEach { type ->
                LoginButton(
                    type = type,
                    onClickLogin = onClickLogin,
                )
            }
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
