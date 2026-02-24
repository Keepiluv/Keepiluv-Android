package com.twix.task_certification.certification.component

import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.components.text.AppText
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle

@Composable
internal fun LoadingContent() {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(CommonColor.White),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(Modifier.height(245.dp))

        FloatingPlane()

        Spacer(Modifier.height(6.dp))

        AppText(
            text = stringResource(R.string.task_certification_loading),
            style = AppTextStyle.H1,
            color = GrayColor.C500,
        )

        Spacer(Modifier.height(10.dp))

        AppText(
            text = stringResource(R.string.task_certification_plz_waiting),
            style = AppTextStyle.T2,
            color = GrayColor.C300,
        )
    }
}

@Composable
private fun FloatingPlane() {
    val transition = rememberInfiniteTransition(label = "plane")

    val offsetY by transition.animateFloat(
        -8f,
        8f,
        infiniteRepeatable(
            tween(1500, easing = EaseInOut),
            RepeatMode.Reverse,
        ),
    )

    val offsetX by transition.animateFloat(
        -6f,
        6f,
        infiniteRepeatable(
            tween(2200, easing = LinearEasing),
            RepeatMode.Reverse,
        ),
    )

    val rotation by transition.animateFloat(
        -6f,
        6f,
        infiniteRepeatable(
            tween(1300, easing = EaseInOut),
            RepeatMode.Reverse,
        ),
    )

    Image(
        painter = painterResource(R.drawable.ic_big_plane),
        contentDescription = null,
        modifier =
            Modifier.graphicsLayer {
                translationX = offsetX
                translationY = offsetY
                rotationZ = rotation
            },
    )
}

@Preview(showBackground = true)
@Composable
private fun LoadingContentPreview() {
    TwixTheme {
        LoadingContent()
    }
}
