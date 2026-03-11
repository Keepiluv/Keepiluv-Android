package com.twix.photolog.capture.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.twix.designsystem.R
import com.twix.designsystem.components.button.AppRoundButton
import com.twix.designsystem.theme.CommonColor
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.domain.model.enums.AppTextStyle
import com.twix.photolog.capture.model.CaptureStatus
import com.twix.ui.extension.noRippleClickable

@Composable
internal fun CameraControlBar(
    capture: CaptureStatus,
    onCaptureClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    onClickGallery: () -> Unit,
    onClickRefresh: () -> Unit,
    onClickUpload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (capture) {
        CaptureStatus.NotCaptured -> {
            ImageNotCapturedBar(
                onCaptureClick = onCaptureClick,
                onToggleCameraClick = onToggleCameraClick,
                onClickGallery = onClickGallery,
                modifier = modifier,
            )
        }

        is CaptureStatus.Captured -> {
            ImageCapturedBar(
                onClickRefresh = onClickRefresh,
                onClickUpload = onClickUpload,
                modifier = modifier,
            )
        }
    }
}

@Composable
private fun ImageNotCapturedBar(
    onCaptureClick: () -> Unit,
    onToggleCameraClick: () -> Unit,
    onClickGallery: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var enabled by remember { mutableStateOf(true) }

    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(horizontal = 45.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_gallery),
            contentDescription = null,
            modifier =
                Modifier
                    .size(56.dp)
                    .noRippleClickable(enabled = enabled, onClick = onClickGallery),
        )

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_camera_shutter),
            contentDescription = null,
            modifier =
                Modifier
                    .noRippleClickable(enabled = enabled) {
                        enabled = false
                        onCaptureClick()
                    },
        )

        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_camera_toggle),
            contentDescription = null,
            modifier =
                Modifier
                    .size(56.dp)
                    .noRippleClickable(enabled = enabled, onClick = onToggleCameraClick),
        )
    }
}

@Composable
private fun ImageCapturedBar(
    onClickRefresh: () -> Unit,
    onClickUpload: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 58.dp),
    ) {
        Image(
            imageVector = ImageVector.vectorResource(R.drawable.ic_camera_retake),
            contentDescription = null,
            modifier =
                Modifier
                    .size(52.dp)
                    .align(Alignment.CenterStart)
                    .noRippleClickable(onClick = onClickRefresh),
        )

        Row(
            modifier =
                Modifier
                    .align(Alignment.Center),
        ) {
            Spacer(modifier = Modifier.width(12.dp))

            AppRoundButton(
                borderColor = CommonColor.White,
                backgroundColor = GrayColor.C500,
                text = stringResource(R.string.photolog_upload),
                textStyle = AppTextStyle.T2,
                textColor = CommonColor.White,
                modifier =
                    Modifier
                        .width(150.dp)
                        .height(74.dp)
                        .noRippleClickable(onClick = onClickUpload),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000)
@Composable
private fun CameraControlBarPreview() {
    TwixTheme {
        Column {
            CameraControlBar(
                capture = CaptureStatus.NotCaptured,
                onCaptureClick = {},
                onToggleCameraClick = {},
                onClickGallery = {},
                onClickRefresh = {},
                onClickUpload = {},
            )

            CameraControlBar(
                capture = CaptureStatus.Captured("".toUri()),
                onCaptureClick = {},
                onToggleCameraClick = {},
                onClickGallery = {},
                onClickRefresh = {},
                onClickUpload = {},
            )
        }
    }
}
