package com.twix.photolog.capture.model.camera

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.twix.photolog.capture.model.TorchStatus
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class CaptureCamera(
    private val context: Context,
) : Camera {
    private var cameraControl: CameraControl? = null
    private var cameraInfo: CameraInfo? = null
    private var cameraProvider: ProcessCameraProvider? = null

    private val _surfaceRequests = MutableStateFlow<CameraPreview?>(null)
    override val surfaceRequests: StateFlow<CameraPreview?> = _surfaceRequests.asStateFlow()

    private val imageCapture: ImageCapture =
        ImageCapture
            .Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
            .setFlashMode(ImageCapture.FLASH_MODE_OFF)
            .setJpegQuality(JPEG_QUALITY)
            .setOutputFormat(ImageCapture.OUTPUT_FORMAT_JPEG)
            .build()

    private val preview: Preview =
        Preview.Builder().build().apply {
            setSurfaceProvider { request ->
                _surfaceRequests.value = CameraPreview(request)
            }
        }

    override suspend fun bind(
        lifecycleOwner: LifecycleOwner,
        lens: CameraSelector,
    ) {
        val provider = cameraProvider ?: ProcessCameraProvider.awaitInstance(context)
        cameraProvider = provider

        provider.unbindAll()

        val camera =
            provider.bindToLifecycle(
                lifecycleOwner,
                lens,
                preview,
                imageCapture,
            )

        cameraControl = camera.cameraControl
        cameraInfo = camera.cameraInfo
    }

    override suspend fun takePicture(torch: TorchStatus): Result<Uri> =
        suspendCancellableCoroutine { continuation ->
            imageCapture.flashMode =
                when (torch) {
                    TorchStatus.On -> ImageCapture.FLASH_MODE_ON
                    TorchStatus.Off -> ImageCapture.FLASH_MODE_OFF
                }

            val contentValues = contentValues()
            val outputOptions = outputFileOptions(contentValues)

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                capture(continuation),
            )
        }

    private fun contentValues(): ContentValues =
        ContentValues().apply {
            put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                IMAGE_NAME.format(System.currentTimeMillis()),
            )
            put(MediaStore.MediaColumns.MIME_TYPE, IMAGE_MIME_TYPE)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, IMAGE_PATH)
            }
        }

    private fun outputFileOptions(contentValues: ContentValues): ImageCapture.OutputFileOptions =
        ImageCapture.OutputFileOptions
            .Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues,
            ).build()

    private fun capture(continuation: CancellableContinuation<Result<Uri>>): ImageCapture.OnImageSavedCallback =
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(result: ImageCapture.OutputFileResults) {
                if (continuation.isActive.not()) return
                val uri = result.savedUri
                if (uri != null) {
                    continuation.resume(Result.success(uri))
                } else {
                    continuation.resume(
                        Result.failure(IllegalStateException(URI_NOT_FOUND_EXCEPTION)),
                    )
                }
            }

            override fun onError(exception: ImageCaptureException) {
                if (continuation.isActive.not()) return
                continuation.resume(Result.failure(exception))
            }
        }

    override fun unbind() {
        cameraProvider?.unbindAll()
        _surfaceRequests.value = null
    }

    companion object Companion {
        private const val JPEG_QUALITY = 95

        private const val IMAGE_MIME_TYPE = "image/jpeg"
        private const val IMAGE_NAME = "task_%d"
        private const val IMAGE_PATH = "Pictures/Photolog"

        private const val URI_NOT_FOUND_EXCEPTION = "촬영한 이미지의 Uri를 찾을 수 없습니다"
    }
}
