package com.twix.photolog.capture.model

import android.net.Uri
import androidx.compose.runtime.Immutable

@Immutable
sealed interface CaptureStatus {
    data class Captured(
        val uri: Uri,
    ) : CaptureStatus

    data object NotCaptured : CaptureStatus
}
