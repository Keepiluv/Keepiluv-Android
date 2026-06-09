package com.twix.util.extension

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import androidx.core.net.toUri

fun Context.openExternalUrl(
    url: String,
    onFailed: () -> Unit = {},
) {
    if (url.isBlank()) {
        onFailed()
        return
    }

    val intent =
        Intent(Intent.ACTION_VIEW, url.toUri()).apply {
            addCategory(Intent.CATEGORY_BROWSABLE)

            if (this@openExternalUrl !is Activity) {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
        }

    try {
        startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        onFailed()
    } catch (e: Exception) {
        onFailed()
    }
}
