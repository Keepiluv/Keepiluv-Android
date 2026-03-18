package com.twix.designsystem.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.components.toast.model.ToastAction
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.domain.model.enums.AppTextStyle
import com.twix.ui.extension.noRippleClickable
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ToastHost(
    toastManager: ToastManager,
    modifier: Modifier = Modifier,
    bottomPadding: Dp = 80.dp,
) {
    var current by remember { mutableStateOf<ToastData?>(null) }
    var visible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    var dismissJob by remember { mutableStateOf<Job?>(null) }
    val animationMs = 200

    // 가장 최신 토스트만을 렌더링하기 위해서 collectLatest를 사용
    LaunchedEffect(toastManager) {
        toastManager.toasts.collectLatest { toast ->
            dismissJob?.cancel()
            current = toast
            visible = true

            dismissJob =
                scope.launch {
                    delay(toast.durationMillis)
                    visible = false
                    delay(animationMs.toLong())
                    if (current == toast) current = null
                }
        }
    }

    // 이 메서드는 보러가기 버튼을 클릭했을 때 토스트가 바로 사라지도록 처리하기 위해서 추가
    fun dismiss() {
        dismissJob?.cancel()
        visible = false
        scope.launch {
            delay(animationMs.toLong())
            current = null
        }
    }

    Box(
        modifier =
            modifier
                .fillMaxSize()
                .imePadding(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = visible && current != null,
            enter =
                slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(animationMs),
                ) + fadeIn(tween(animationMs)),
            exit =
                slideOutVertically(
                    targetOffsetY = { it },
                    animationSpec = tween(animationMs),
                ) + fadeOut(tween(animationMs)),
        ) {
            current?.let { toast ->
                ToastItem(
                    data = toast,
                    bottomPadding = bottomPadding,
                    onDismiss = ::dismiss,
                )
            }
        }
    }
}

@Composable
private fun ToastItem(
    data: ToastData,
    bottomPadding: Dp,
    onDismiss: () -> Unit,
) {
    val res =
        when (data.type) {
            ToastType.SUCCESS -> painterResource(R.drawable.ic_toast_success)
            ToastType.DELETE -> painterResource(R.drawable.ic_toast_delete)
            ToastType.LIKE -> painterResource(R.drawable.ic_toast_heart)
            ToastType.ERROR -> painterResource(R.drawable.ic_toast_warning)
        }

    Surface(
        modifier =
            Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = bottomPadding)
                .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, GrayColor.C500),
        color = GrayColor.C400,
    ) {
        Row(
            modifier =
                Modifier
                    .padding(vertical = 12.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Image(
                painter = res,
                contentDescription = "toast icon",
                modifier = Modifier.size(24.dp),
            )

            Spacer(Modifier.width(3.5.dp))

            AppText(
                text = data.message,
                style = AppTextStyle.B1,
                color = CommonColor.White,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Start,
            )

            data.action?.let {
                ActionButton(it, onDismiss)
            }
        }
    }
}

@Composable
private fun ActionButton(
    action: ToastAction,
    onDismiss: () -> Unit,
) {
    AppText(
        text = action.label,
        style = AppTextStyle.B1,
        color = CommonColor.White,
        modifier =
            Modifier
                .background(GrayColor.C300, RoundedCornerShape(8.dp))
                .border(1.dp, GrayColor.C500, RoundedCornerShape(8.dp))
                .padding(vertical = 5.5.dp, horizontal = 12.dp)
                .noRippleClickable(
                    onClick = {
                        action.onClick()
                        onDismiss()
                    },
                ),
    )
}
