package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class StatsDetailRefreshBus {
    enum class Publisher {
        GoalUpdated,
    }

    private val _events =
        MutableSharedFlow<Publisher>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    val events: SharedFlow<Publisher> = _events

    fun notifyChanged(publisher: Publisher) = _events.tryEmit(publisher)
}
