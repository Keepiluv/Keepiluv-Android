package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class StatsRefreshBus {
    enum class Target {
        InProgress,
        End,
        All,
    }

    private val _events =
        MutableSharedFlow<Target>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    val events: SharedFlow<Target> = _events

    fun notifyChanged(target: Target) = _events.tryEmit(target)
}
