package com.twix.task_certification.certification.component

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.twix.designsystem.R
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.task_certification.certification.model.camera.CameraPreview
import com.twix.task_certification.certification.model.CaptureStatus
import com.twix.task_certification.certification.model.TorchStatus
import com.twix.ui.extension.noRippleClickable

@Composable
fun CameraPreviewBox(
    showTorch: Boolean,
    capture: CaptureStatus,
    previewRequest: CameraPreview?,
    torch: TorchStatus,
    onClickFlash: () -> Unit,
    onPositioned: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .size(375.66.dp)
                .padding(horizontal = 5.dp)
                .onGloballyPositioned { coordinates ->
                    onPositioned(coordinates.boundsInParent().bottom)
                }.border(
                    color = GrayColor.C400,
                    width = 2.dp,
                    shape = RoundedCornerShape(73.83.dp),
                ).clip(RoundedCornerShape(73.83.dp)),
    ) {
        CameraSurface(capture, previewRequest)

        if (showTorch) {
            TorchIcon(torch, onClickFlash)
        }
    }
}

@Composable
private fun CameraSurface(
    capture: CaptureStatus,
    previewRequest: CameraPreview?,
) {
    when (capture) {
        CaptureStatus.NotCaptured -> {
            previewRequest?.let {
                CameraXViewfinder(
                    surfaceRequest = it.request,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }

        is CaptureStatus.Captured -> {
            AsyncImage(
                model = capture.uri,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        }
    }
}

@Composable
private fun TorchIcon(
    torch: TorchStatus,
    onClickFlash: () -> Unit,
) {
    val torchIcon =
        when (torch) {
            TorchStatus.On -> ImageVector.vectorResource(id = R.drawable.ic_camera_torch_on)
            TorchStatus.Off -> ImageVector.vectorResource(id = R.drawable.ic_camera_torch_off)
        }

    Image(
        imageVector = torchIcon,
        contentDescription = null,
        modifier =
            Modifier
                .noRippleClickable(onClick = onClickFlash)
                .padding(start = 30.33.dp, top = 31.82.dp),
    )
}

@Preview
@Composable
fun CameraPreviewBoxNotCapturedPreview() {
    TwixTheme {
        CameraPreviewBox(
            capture = CaptureStatus.NotCaptured,
            showTorch = true,
            torch = TorchStatus.Off,
            previewRequest = null,
            onClickFlash = {},
            onPositioned = {},
        )
    }
}
