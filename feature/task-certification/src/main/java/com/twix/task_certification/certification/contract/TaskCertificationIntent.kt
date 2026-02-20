package com.twix.task_certification.certification.contract

import android.net.Uri
import com.twix.ui.base.Intent

sealed interface TaskCertificationIntent : Intent {
    data class TakePicture(
        val uri: Uri?,
    ) : TaskCertificationIntent

    data class PickPicture(
        val uri: Uri?,
    ) : TaskCertificationIntent

    data object ToggleLens : TaskCertificationIntent

    data object ToggleTorch : TaskCertificationIntent

    data object RetakePicture : TaskCertificationIntent

    data class UpdateComment(
        val value: String,
    ) : TaskCertificationIntent

    data class CommentFocusChanged(
        val isFocused: Boolean,
    ) : TaskCertificationIntent

    data object TryUpload : TaskCertificationIntent

    data class Upload(
        val image: ByteArray,
    ) : TaskCertificationIntent {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Upload

            return image.contentEquals(other.image)
        }

        override fun hashCode(): Int = image.contentHashCode()
    }
}
