package com.twix.util.bus

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class GoalRefreshBus {
    // 홈 화면 목표 리프레쉬 이벤트
    private val _goalEvents =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
        )

    // 편집 화면 목표 리프레쉬 이벤트
    private val _goalSummariesEvents =
        MutableSharedFlow<Unit>(
            replay = 0,
            extraBufferCapacity = 1,
        )
    val goalEvents: SharedFlow<Unit> = _goalEvents
    val goalSummariesEvents: SharedFlow<Unit> = _goalSummariesEvents

    fun notifyGoalListChanged() = _goalEvents.tryEmit(Unit)

    fun notifyGoalSummariesChanged() = _goalSummariesEvents.tryEmit(Unit)
}
