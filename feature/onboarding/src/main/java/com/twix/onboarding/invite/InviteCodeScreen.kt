package com.twix.onboarding.invite

import android.content.ClipData
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboard
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.toClipEntry
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.onboarding.OnBoardingViewModel
import com.twix.onboarding.invite.component.InviteCodeTextField
import com.twix.onboarding.model.OnBoardingIntent
import com.twix.onboarding.model.OnBoardingSideEffect
import com.twix.ui.base.ObserveAsEvents
import com.twix.ui.extension.noRippleClickable
import com.twix.ui.keyboard.Keyboard
import com.twix.ui.keyboard.keyboardAsState
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun InviteCodeRoute(
    viewModel: OnBoardingViewModel,
    navigateToNext: () -> Unit,
    navigateToBack: () -> Unit,
    toastManager: ToastManager = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val keyboardState by keyboardAsState()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    val clipboard = LocalClipboard.current

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
            OnBoardingSideEffect.InviteCode.NavigateToNext -> navigateToNext()
            else -> Unit
        }
    }

    InviteCodeScreen(
        uiModel = uiState.inviteCode,
        keyboardState = keyboardState,
        navigateToBack = navigateToBack,
        onChangeInviteCode = { viewModel.dispatch(OnBoardingIntent.WriteInviteCode(it)) },
        onComplete = { viewModel.dispatch(OnBoardingIntent.ConnectCouple) },
        onCopyInviteCode = {
            val clipData =
                ClipData.newPlainText(
                    "inviteCode",
                    uiState.inviteCode.myInviteCode,
                )
            coroutineScope.launch {
                clipboard.setClipEntry(clipData.toClipEntry())
            }
            viewModel.dispatch(OnBoardingIntent.CopyInviteCode)
        },
    )
}

@Composable
private fun InviteCodeScreen(
    uiModel: InviteCodeUiModel,
    keyboardState: Keyboard,
    navigateToBack: () -> Unit,
    onChangeInviteCode: (String) -> Unit,
    onComplete: () -> Unit,
    onCopyInviteCode: () -> Unit,
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(72.dp)
                    .padding(horizontal = 10.dp, vertical = 14.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Image(
                imageVector = ImageVector.vectorResource(com.twix.designsystem.R.drawable.ic_arrow_m_left),
                contentDescription = null,
                modifier =
                    Modifier
                        .size(44.dp)
                        .noRippleClickable(onClick = navigateToBack),
            )
        }

        Column {
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(
                visible = keyboardState == Keyboard.Closed,
                enter = fadeIn(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                AppText(
                    text = stringResource(R.string.onboarding_invite_code_plz_write_invite_code),
                    style = AppTextStyle.H3,
                    color = GrayColor.C500,
                    modifier = Modifier.padding(start = 24.dp, top = 80.dp),
                )
            }

            Spacer(modifier = Modifier.height(92.dp))

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(103.dp)
                        .padding(horizontal = 36.dp)
                        .border(
                            color = GrayColor.C200,
                            width = 1.dp,
                            shape = RoundedCornerShape(12.dp),
                        ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AppText(
                    text = stringResource(R.string.onboarding_invite_code_my_invite_code),
                    style = AppTextStyle.B3,
                    color = GrayColor.C400,
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AppText(
                        text = uiModel.myInviteCode,
                        style = AppTextStyle.H1,
                        color = GrayColor.C500,
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Image(
                        imageVector = ImageVector.vectorResource(R.drawable.ic_copy),
                        contentDescription = null,
                        modifier = Modifier.noRippleClickable(onClick = onCopyInviteCode),
                    )
                }
            }

            Spacer(modifier = Modifier.height(52.dp))

            AppText(
                text = stringResource(R.string.onboarding_invite_code_write_invite_code),
                style = AppTextStyle.B3,
                color = GrayColor.C500,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(12.dp))

            InviteCodeTextField(
                inviteCode = uiModel.partnerInviteCode,
                onValueChange = onChangeInviteCode,
                modifier =
                    Modifier
                        .focusRequester(focusRequester)
                        .align(Alignment.CenterHorizontally),
            )
        }

        AppButton(
            text = stringResource(R.string.onboarding_profile_button_title),
            onClick = { onComplete() },
            backgroundColor = if (uiModel.isValid) GrayColor.C500 else GrayColor.C100,
            textColor = if (uiModel.isValid) CommonColor.White else GrayColor.C300,
            enabled = uiModel.isValid,
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 20.dp)
                    .imePadding(),
        )
    }
}

@Preview(name = "InviteCodeScreen", showBackground = true)
@Composable
private fun InviteCodeScreenPreview() {
    TwixTheme {
        InviteCodeScreen(
            uiModel = InviteCodeUiModel(),
            onChangeInviteCode = {},
            onComplete = {},
            navigateToBack = {},
            keyboardState = Keyboard.Opened,
            onCopyInviteCode = {},
        )
    }
}
