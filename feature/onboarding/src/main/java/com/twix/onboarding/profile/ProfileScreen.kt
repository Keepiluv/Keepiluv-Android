package com.twix.onboarding.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.text_field.UnderlineTextField
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.SystemColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.onboarding.OnBoardingViewModel
import com.twix.onboarding.contract.OnBoardingIntent
import com.twix.onboarding.contract.OnBoardingSideEffect
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import org.koin.compose.koinInject

@Composable
fun ProfileRoute(
    viewModel: OnBoardingViewModel,
    navigateToDday: () -> Unit,
    navigateToHome: () -> Unit,
    toastManager: ToastManager = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            is OnBoardingSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(sideEffect.message),
                        type = sideEffect.type,
                    ),
                )
            }

            OnBoardingSideEffect.ProfileSetting.NavigateToHome -> navigateToHome()
            OnBoardingSideEffect.ProfileSetting.NavigateToNext -> navigateToDday()
            else -> Unit
        }
    }

    ProfileScreen(
        uiModel = uiState.profile,
        onCompleted = {
            viewModel.dispatch(OnBoardingIntent.SubmitNickName)
        },
        onChangeNickName = { viewModel.dispatch(OnBoardingIntent.WriteNickName(it)) },
    )
}

@Composable
private fun ProfileScreen(
    uiModel: ProfileUiModel,
    onCompleted: () -> Unit,
    onChangeNickName: (String) -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = CommonColor.White)
                .imePadding(),
    ) {
        Spacer(modifier = Modifier.height(80.dp))

        AppText(
            text = stringResource(R.string.onboarding_profile_title),
            style = AppTextStyle.H3,
            color = GrayColor.C500,
            modifier = Modifier.padding(start = 24.dp),
        )

        Spacer(modifier = Modifier.height(32.dp))

        UnderlineTextField(
            value = uiModel.nickname,
            placeHolder = stringResource(R.string.onboarding_name_placeholder),
            showTrailing = true,
            onValueChange = onChangeNickName,
            trailing = {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_clear_text),
                    contentDescription = null,
                    modifier = Modifier.noRippleClickable { onChangeNickName("") },
                )
            },
            modifier =
                Modifier
                    .focusRequester(focusRequester)
                    .padding(horizontal = 20.dp),
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = ImageVector.vectorResource(R.drawable.ic_check_success),
                contentDescription = null,
                tint = if (uiModel.isValid) SystemColor.Success else GrayColor.C300,
            )

            Spacer(modifier = Modifier.width(4.dp))

            AppText(
                style = AppTextStyle.C2,
                color = if (uiModel.isValid) SystemColor.Success else GrayColor.C300,
                text = stringResource(id = R.string.onboarding_name_helper),
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        AppButton(
            text = stringResource(R.string.onboarding_profile_button_title),
            onClick = { onCompleted() },
            backgroundColor = if (uiModel.isValid) GrayColor.C500 else GrayColor.C100,
            textColor = if (uiModel.isValid) CommonColor.White else GrayColor.C300,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun UnValidProfileScreenPreview() {
    TwixTheme {
        Row {
            ProfileScreen(
                onCompleted = {},
                onChangeNickName = {},
                uiModel =
                    ProfileUiModel(
                        nickname = "",
                        isValid = false,
                    ),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ValidProfileScreenPreview() {
    TwixTheme {
        ProfileScreen(
            onCompleted = {},
            onChangeNickName = {},
            uiModel =
                ProfileUiModel(
                    nickname = "",
                    isValid = true,
                ),
        )
    }
}
