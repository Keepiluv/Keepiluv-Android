package com.twix.navigation.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController

@Composable
fun rememberNavGraphOwner(
    navController: NavHostController,
    graphRoute: String,
    currentEntry: NavBackStackEntry,
): NavBackStackEntry =
    remember(currentEntry, graphRoute) {
        navController.getBackStackEntry(graphRoute)
    }
