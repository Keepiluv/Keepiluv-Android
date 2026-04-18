package com.twix.ui.base

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class SideEffectHolder<S : SideEffect> {
    private val channel = Channel<S>(Channel.BUFFERED)
    val flow = channel.receiveAsFlow()

    suspend fun emit(effect: S) {
        channel.send(effect)
    }

    fun tryEmit(effect: S) {
        channel.trySend(effect)
    }
}
