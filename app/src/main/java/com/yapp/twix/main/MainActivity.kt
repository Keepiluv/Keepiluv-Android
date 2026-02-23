package com.yapp.twix.main

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.twix.designsystem.components.toast.ToastHost
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.theme.TwixTheme
import com.twix.navigation.AppNavHost
import com.twix.navigation_contract.NotificationLaunchEventSource
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val notificationLaunchEventSource: NotificationLaunchEventSource by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)
        enableEdgeToEdge()
        setContent {
            val toastManager: ToastManager = koinInject()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

            TwixTheme {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(
                                WindowInsets.systemBars.only(WindowInsetsSides.Vertical),
                            ),
                ) {
                    AppNavHost(notificationLaunchEventSource = notificationLaunchEventSource)

                    ToastHost(
                        toastManager = toastManager,
                        modifier =
                            Modifier
                                .align(Alignment.BottomCenter),
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        notificationLaunchEventSource.dispatchFromIntent(intent)
    }
}
