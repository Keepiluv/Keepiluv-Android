package com.twix.main.model

import androidx.compose.runtime.Immutable
import com.twix.ui.base.State

@Immutable
data class MainUiState(
    val selectedTab: MainTab = MainTab.HOME,
) : State
