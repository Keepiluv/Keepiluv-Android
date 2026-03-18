package com.twix.photolog.capture.component

import androidx.camera.compose.CameraXViewfinder
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.twix.designsystem.theme.GrayColor
import com.twix.designsystem.theme.TwixTheme
import com.twix.photolog.capture.model.CaptureStatus
import com.twix.photolog.capture.model.TorchStatus
import com.twix.photolog.capture.model.camera.CameraPreview

@Composable
fun CameraPreviewBox(
    showTorch: Boolean,
    capture: CaptureStatus,
    previewRequest: CameraPreview?,
    torch: TorchStatus,
    onClickTorch: () -> Unit,
    onPositioned: (Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f)
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
            TorchButton(
                torch = torch,
                onClickTorch = onClickTorch,
                modifier = Modifier.padding(top = 31.dp, start = 30.dp),
            )
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

@Preview
@Composable
fun CameraPreviewBoxNotCapturedPreview() {
    TwixTheme {
        CameraPreviewBox(
            capture = CaptureStatus.NotCaptured,
            showTorch = true,
            torch = TorchStatus.Off,
            previewRequest = null,
            onClickTorch = {},
            onPositioned = {},
        )
    }
}
