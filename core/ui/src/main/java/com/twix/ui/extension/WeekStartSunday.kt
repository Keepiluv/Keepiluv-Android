package com.twix.ui.extension

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * 주 시작을 일요일로 맞추는 메서드
 */
fun LocalDate.weekStartSunday(): LocalDate = this.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
