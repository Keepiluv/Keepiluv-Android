package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class StatsRefreshBus {
    /**
     * 통계 목록 갱신 대상을 나타내는 열거형 클래스.
     *
     * [notifyChanged]의 인자로 전달되어 갱신할 통계 탭을 결정한다.
     */
    enum class Target {
        /** 진행 중인 목표 카드만 갱신 */
        InProgress,

        /** 완료된 목표 카드만 갱신 */
        End,

        /** 진행 중 및 완료된 목표 카드 전체 갱신 */
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
