package com.twix.onboarding.dday

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.twix.designsystem.R
import com.twix.designsystem.components.bottomsheet.CommonBottomSheet
import com.twix.designsystem.components.bottomsheet.model.CommonBottomSheetConfig
import com.twix.designsystem.components.button.AppButton
import com.twix.designsystem.components.calendar.Calendar
import com.twix.designsystem.components.loading.TwixLoadingOverlay
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.onboarding.OnBoardingViewModel
import com.twix.onboarding.contract.OnBoardingIntent
import com.twix.onboarding.contract.OnBoardingSideEffect
import com.twix.onboarding.dday.component.DDayField
import com.twix.onboarding.dday.component.DdayTopBar
import com.twix.ui.base.ObserveAsEvents
import org.koin.compose.koinInject
import java.time.LocalDate

@Composable
fun DdayRoute(
    viewModel: OnBoardingViewModel,
    navigateToHome: () -> Unit,
    navigateToBack: () -> Unit,
    toastManager: ToastManager = koinInject(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val currentContext by rememberUpdatedState(context)
    var showCalendarBottomSheet by remember { mutableStateOf(false) }

    ObserveAsEvents(viewModel.sideEffect) { sideEffect ->
        when (sideEffect) {
            OnBoardingSideEffect.DdaySetting.NavigateToHome -> navigateToHome()
            is OnBoardingSideEffect.ShowToast -> {
                toastManager.tryShow(
                    ToastData(
                        message = currentContext.getString(sideEffect.message),
                        type = sideEffect.type,
                    ),
                )
            }

            else -> Unit
        }
    }

    DdayScreen(
        uiModel = uiState.dDay,
        showLoadingOverlay = uiState.isSubmittingDday,
        onCompleted = { viewModel.dispatch(OnBoardingIntent.SubmitDday) },
        onClickBack = navigateToBack,
        onDateClick = { showCalendarBottomSheet = true },
        showCalendarBottomSheet = showCalendarBottomSheet,
        onDismissCalendar = { showCalendarBottomSheet = false },
        onDateSelected = {
            showCalendarBottomSheet = false
            viewModel.dispatch(OnBoardingIntent.SelectDate(it))
        },
    )
}

@Composable
fun DdayScreen(
    uiModel: DdayUiModel,
    showLoadingOverlay: Boolean,
    onCompleted: () -> Unit,
    onClickBack: () -> Unit,
    onDateClick: () -> Unit,
    showCalendarBottomSheet: Boolean,
    onDismissCalendar: () -> Unit,
    onDateSelected: (LocalDate) -> Unit,
) {
    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(color = CommonColor.White)
                .statusBarsPadding(),
    ) {
        Column {
            DdayTopBar(navigateToBack = onClickBack)

            Spacer(modifier = Modifier.height(8.dp))

            AppText(
                text = stringResource(R.string.onboarding_dday_plz_set_dday),
                style = AppTextStyle.H3,
                color = GrayColor.C500,
                modifier = Modifier.padding(start = 24.dp),
            )

            Spacer(modifier = Modifier.height(32.dp))

            DDayField(
                uiModel = uiModel,
                onDateClick = onDateClick,
            )

            Spacer(Modifier.weight(1f))

            AppButton(
                text = stringResource(R.string.onboarding_profile_button_title),
                onClick = { onCompleted() },
                backgroundColor = if (uiModel.isSelected) GrayColor.C500 else GrayColor.C100,
                textColor = if (uiModel.isSelected) CommonColor.White else GrayColor.C300,
                enabled = uiModel.isSelected,
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
            )
        }

        CommonBottomSheet(
            visible = showCalendarBottomSheet,
            config = CommonBottomSheetConfig(showHandle = false),
            onDismissRequest = onDismissCalendar,
        ) {
            Calendar(
                initialDate = uiModel.anniversaryDate,
                onComplete = onDateSelected,
            )
        }

        if (showLoadingOverlay) {
            TwixLoadingOverlay()
        }
    }
}

@Preview
@Composable
fun DdayScreenPreview() {
    TwixTheme {
        DdayScreen(
            uiModel =
                DdayUiModel(
                    anniversaryDate = LocalDate.now(),
                ),
            showLoadingOverlay = false,
            onClickBack = {},
            onDateClick = {},
            showCalendarBottomSheet = false,
            onDismissCalendar = {},
            onDateSelected = {},
            onCompleted = {},
        )
    }
}
