package com.twix.navigation_contract

/**
 * 구현체는 :core:navigation -> NotificationRouter
 * */
interface NotificationDeepLinkHandler {
    fun handle(
        rawDeepLink: String,
        navigator: AppNavigator,
    )
}
