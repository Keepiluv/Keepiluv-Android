package com.twix.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import org.koin.compose.viewmodel.koinViewModel

@Composable
inline fun <reified VM : ViewModel> NavBackStackEntry.graphViewModel(
    navController: NavHostController,
    route: String,
): VM {
    val parentEntry =
        remember(this) {
            navController.getBackStackEntry(route)
        }

    return koinViewModel(viewModelStoreOwner = parentEntry)
}
