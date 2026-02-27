package com.twix.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.button.LoginButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.OnboardingStatus
import com.twix.domain.model.enums.AppTextStyle
import com.twix.domain.model.enums.LoginType
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
    var imageBottomPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val offsetPx = with(density) { 34.dp.toPx() }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
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

        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_keepi_singing),
                contentDescription = null,
                modifier =
                    Modifier
                        .onGloballyPositioned { coordinates ->
                            imageBottomPx = coordinates.boundsInParent().bottom
                        },
            )

            if (imageBottomPx != 0f) {
                Column(
                    modifier =
                        Modifier
                            .padding(horizontal = 20.dp)
                            .offset {
                                IntOffset(
                                    x = 0,
                                    /**
                                     * singing 이미지 하단 기준으로 로그인 버튼을 배치하고
                                     * 이미지와 버튼이 겹치는 만큼(34dp) 상단으로 이동
                                     * */
                                    y = (imageBottomPx - offsetPx).toInt(),
                                )
                            },
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
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginScreenPreview() {
    TwixTheme {
        LoginScreen(onClickLogin = {})
    }
}
