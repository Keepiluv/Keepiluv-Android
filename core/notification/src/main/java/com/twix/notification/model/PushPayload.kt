package com.twix.notification.model

data class PushPayload(
    val title: String?,
    val body: String?,
    val deepLink: String?,
)

fun Map<String, String>.toTwixPushPayload() =
    PushPayload(
        title = this["title"],
        body = this["body"],
        deepLink = this["deepLink"],
    )
