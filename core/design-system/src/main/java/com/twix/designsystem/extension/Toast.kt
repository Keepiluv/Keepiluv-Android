package com.twix.designsystem.extension

import android.content.Context
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.components.toast.model.ToastAction
import com.twix.designsystem.components.toast.model.ToastData
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.ui.extension.openAppSettings

suspend fun ToastManager.showCameraPermissionToastWithNavigateToSettingAction(context: Context) {
    show(
        ToastData(
            message = context.getString(R.string.toast_camera_permission_request),
            type = ToastType.ERROR,
            action =
                ToastAction(
                    label = context.getString(R.string.word_setting),
                    onClick = { context.openAppSettings() },
                ),
        ),
    )
}
