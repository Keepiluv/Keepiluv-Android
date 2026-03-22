package com.yapp.twix.main

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.twix.designsystem.components.toast.ToastHost
import com.twix.designsystem.components.toast.ToastManager
import com.twix.designsystem.theme.TwixTheme
import com.twix.navigation.AppNavHost
import com.twix.navigation_contract.InviteLaunchEventSource
import com.twix.navigation_contract.NotificationLaunchEventSource
import org.koin.android.ext.android.inject
import org.koin.compose.koinInject
import kotlin.getValue

class MainActivity : ComponentActivity() {
    private val notificationLaunchEventSource: NotificationLaunchEventSource by inject()
    private val inviteLaunchEventSource: InviteLaunchEventSource by inject()
    private val requestNotificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleNotificationIntent(intent)
        inviteLaunchEventSource.dispatchFromIntent(intent)
        enableEdgeToEdge()
        setContent {
            val toastManager: ToastManager = koinInject()
            WindowCompat.setDecorFitsSystemWindows(window, false)
            WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

            LaunchedEffect(Unit) {
                requestNotificationPermissionIfNeeded()
            }

            TwixTheme {
                Box(
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(
                                WindowInsets.systemBars.only(WindowInsetsSides.Vertical),
                            ),
                ) {
                    AppNavHost(
                        notificationLaunchEventSource = notificationLaunchEventSource,
                    )

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
        inviteLaunchEventSource.dispatchFromIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        notificationLaunchEventSource.dispatchFromIntent(intent)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val isGranted =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS,
            ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) return

        requestNotificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}
