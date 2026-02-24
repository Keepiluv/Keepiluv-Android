package com.twix.task_certification.certification.model.camera

import androidx.camera.core.SurfaceRequest
import androidx.compose.runtime.Immutable

@Immutable
data class CameraPreview(
    val request: SurfaceRequest,
)
