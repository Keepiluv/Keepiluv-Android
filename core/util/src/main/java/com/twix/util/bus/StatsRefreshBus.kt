package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class StatsRefreshBus {
    enum class Publisher {
        InProgress,
        End,
    }

    private val _events =
        MutableSharedFlow<Publisher>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    val events: SharedFlow<Publisher> = _events

    fun notifyChanged(publisher: Publisher) = _events.tryEmit(publisher)
}
