package com.twix.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.twix.designsystem.R
import com.twix.designsystem.theme.CommonColor
import com.twix.domain.model.OnboardingStatus
import com.twix.splash.contract.SplashSideEffect
import com.twix.ui.base.ObserveAsEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = koinViewModel(),
    navigateToMain: () -> Unit,
    navigateToLogin: () -> Unit,
    navigateToOnBoarding: (OnboardingStatus) -> Unit,
) {
    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            SplashSideEffect.NavigateToMain -> navigateToMain()
            SplashSideEffect.NavigateToLogin -> navigateToLogin()
            is SplashSideEffect.NavigateToOnBoarding -> navigateToOnBoarding(sideEffect.status)
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(painter = painterResource(R.drawable.ic_splash), contentDescription = null)
    }
}
