package com.twix.photolog.capture.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.twix.designsystem.R
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.photolog.capture.model.TorchStatus
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun TorchButton(
    torch: TorchStatus,
    onClickTorch: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val torchIcon =
        when (torch) {
            TorchStatus.On -> R.drawable.ic_camera_torch_on
            TorchStatus.Off -> R.drawable.ic_camera_torch_off
        }

    Box(
        modifier =
            modifier
                .size(44.dp)
                .dropShadow(
                    shape = CircleShape,
                    shadow =
                        Shadow(
                            radius = 10.67.dp,
                            spread = 0.dp,
                            color = CommonColor.Black.copy(alpha = 0.3f),
                            offset = DpOffset(0.dp, 0.dp),
                        ),
                ).background(
                    color = CommonColor.White.copy(alpha = 0.1f),
                    shape = CircleShape,
                ).noRippleClickable(onClick = onClickTorch),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            painter = painterResource(torchIcon),
            contentDescription = "Flash Icon",
            tint = CommonColor.White,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFD9D9D9)
@Composable
private fun TorchButtonPreview() {
    TwixTheme {
        Column {
            TorchButton(
                torch = TorchStatus.On,
                onClickTorch = {},
            )
            TorchButton(
                torch = TorchStatus.Off,
                onClickTorch = {},
            )
        }
    }
}
