package com.twix.task_certification.certification.model.camera

import android.net.Uri
import androidx.camera.core.CameraSelector
import androidx.lifecycle.LifecycleOwner
import com.twix.task_certification.certification.model.TorchStatus
import kotlinx.coroutines.flow.StateFlow

interface Camera {
    val surfaceRequests: StateFlow<CameraPreview?>

    suspend fun bind(
        lifecycleOwner: LifecycleOwner,
        lens: CameraSelector,
    )

    fun unbind()

    suspend fun takePicture(): Result<Uri>

    fun toggleTorch(torch: TorchStatus)
}
