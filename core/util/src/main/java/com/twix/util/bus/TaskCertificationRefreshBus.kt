package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class TaskCertificationRefreshBus {
    enum class Publisher {
        PHOTOLOG,
        EDITOR,
    }

    private val _events =
        MutableSharedFlow<Publisher>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    val events: SharedFlow<Publisher> = _events.asSharedFlow()

    fun notifyChanged(value: Publisher) = _events.tryEmit(value)
}
