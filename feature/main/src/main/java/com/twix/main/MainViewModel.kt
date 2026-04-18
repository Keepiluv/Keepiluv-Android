package com.twix.main

import com.twix.main.model.MainTab
import com.twix.main.model.MainUiState
import com.twix.ui.base.BaseViewModel
import com.twix.ui.base.NoSideEffect

class MainViewModel : BaseViewModel<MainUiState, MainIntent, NoSideEffect>(MainUiState()) {
    override suspend fun handleIntent(intent: MainIntent) {
        when (intent) {
            is MainIntent.SelectTab -> selectTab(intent.tab)
        }
    }

    private fun selectTab(tab: MainTab) {
        if (currentState.selectedTab == tab) return

        reduce { copy(selectedTab = tab) }
    }
}
