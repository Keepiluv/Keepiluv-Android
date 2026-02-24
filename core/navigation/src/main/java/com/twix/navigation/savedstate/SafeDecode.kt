package com.twix.navigation.savedstate

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import co.touchlab.kermit.Logger
import kotlinx.serialization.json.Json

inline fun <reified T> SavedStateHandle.decodeNavArgs(key: String): T {
    val raw = get<String>(key)

    try {
        val decoded =
            raw?.let(Uri::decode)
                ?: error("Missing nav arg: $key")

        Logger.d { "NavArgs[$key] decoded=$decoded" }

        return Json.decodeFromString(decoded)
    } catch (e: Exception) {
        Logger.e(e) { "NavArgs[$key] decode failed raw=$raw" }
        throw e
    }
}
