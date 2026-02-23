package com.twix.notification.routing

import android.content.Intent
import com.twix.navigation_contract.NotificationLaunchEventSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * SharedFlow, Channel을 사용하지 않은 이유
 * · 푸시 클릭 발생 상황에서 UI가 아직 준비되지 않은 상태일 수 있음
 * · 이를 방지하기 위해 최신 값 1개 보관 + 구독자가 늦게 붙어도 최신 값 전달 + 명시적 소비가 필요함(StateFlow)
 * */
class NotificationLaunchDispatcher : NotificationLaunchEventSource {
    private val _pendingDeepLink = MutableStateFlow<String?>(null)
    override val pendingDeepLink: StateFlow<String?> = _pendingDeepLink.asStateFlow()

    // MainActivity의 onCreate/onNewIntent에서 중복 호출 방지
    private var lastDispatchedDeepLink: String? = null
    private var lastDispatchedAtMillis: Long = 0L

    override fun dispatchFromIntent(intent: Intent?) {
        if (intent == null) return

        val isPushClick = intent.getBooleanExtra(EXTRA_FROM_PUSH_CLICK, false)
        val deepLink = intent.getStringExtra(EXTRA_DEEP_LINK)

        if (!isPushClick || deepLink.isNullOrBlank()) return

        if (shouldIgnoreDuplicate(deepLink)) return

        _pendingDeepLink.value = deepLink
    }

    override fun consumePendingDeepLink(expected: String?) {
        val current = _pendingDeepLink.value ?: return
        if (expected == null || current == expected) {
            _pendingDeepLink.value = null
        }
    }

    private fun shouldIgnoreDuplicate(deepLink: String): Boolean {
        val now = System.currentTimeMillis()
        val isDuplicate =
            lastDispatchedDeepLink == deepLink && (now - lastDispatchedAtMillis) < 1500L

        if (!isDuplicate) {
            lastDispatchedDeepLink = deepLink
            lastDispatchedAtMillis = now
        }
        return isDuplicate
    }

    companion object {
        const val ACTION_NOTIFICATION_CLICK = "com.twix.notification.ACTION_CLICK"
        const val EXTRA_DEEP_LINK = "extra_deep_link"
        const val EXTRA_FROM_PUSH_CLICK = "extra_from_push_click"
    }
}
