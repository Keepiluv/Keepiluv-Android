package com.twix.photolog.capture.contract

import android.net.Uri
import com.twix.ui.base.Intent

sealed interface PhotologCaptureIntent : Intent {
    data class TakePicture(
        val uri: Uri?,
    ) : PhotologCaptureIntent

    data class PickPicture(
        val uri: Uri?,
    ) : PhotologCaptureIntent

    data object ToggleLens : PhotologCaptureIntent

    data object ToggleTorch : PhotologCaptureIntent

    data object RetakePicture : PhotologCaptureIntent

    data class UpdateComment(
        val value: String,
    ) : PhotologCaptureIntent

    data class CommentFocusChanged(
        val isFocused: Boolean,
    ) : PhotologCaptureIntent

    data object TryUpload : PhotologCaptureIntent

    data class Upload(
        val image: ByteArray,
    ) : PhotologCaptureIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Upload

            return image.contentEquals(other.image)
        }

        override fun hashCode(): Int = image.contentHashCode()
    }
}
