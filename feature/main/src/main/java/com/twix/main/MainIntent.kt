package com.twix.main

import com.twix.main.model.MainTab
import com.twix.ui.base.Intent

sealed interface MainIntent : Intent {
    data class SelectTab(
        val tab: MainTab,
    ) : MainIntent
}
