package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class StatsDetailRefreshBus {
    private val _events =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    val events: SharedFlow<Unit> = _events

    fun notifyChanged() = _events.tryEmit(Unit)
}
