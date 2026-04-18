package com.twix.designsystem.components.toast

import com.twix.designsystem.components.toast.model.ToastData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class ToastManager {
    private val _toasts =
        MutableSharedFlow<ToastData>(
            replay = 0,
            extraBufferCapacity = 16, // 최대 16개까지 토스트를 버퍼에 저장
            onBufferOverflow = BufferOverflow.DROP_OLDEST, // 버퍼 가득차면 가장 오래된 것을 버림
        )
    val toasts: SharedFlow<ToastData> = _toasts

    /**
     * UX상으로 토스트가 절대 유실되어서는 안되는 경우에는 show를 쓰면 됨.
     * */
    suspend fun show(data: ToastData) {
        _toasts.emit(data)
    }

    /**
     * 대부분의 경우에는 tryShow를 쓰면 됨.
     * */
    fun tryShow(data: ToastData): Boolean = _toasts.tryEmit(data)
}
