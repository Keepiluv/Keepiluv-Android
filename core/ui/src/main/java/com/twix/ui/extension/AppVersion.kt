package com.twix.ui.extension

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun Context.getAppVersion(): String {
    val pm = packageManager
    val pkg = packageName
    val info =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(pkg, PackageManager.PackageInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(pkg, 0)
        }

    return info.versionName.orEmpty()
}
