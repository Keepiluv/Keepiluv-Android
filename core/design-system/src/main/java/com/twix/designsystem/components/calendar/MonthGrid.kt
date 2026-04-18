package com.twix.designsystem.components.calendar

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class MonthGrid(
    val month: YearMonth,
    val days: List<LocalDate>,
    val weeks: Int,
)

/**
 * 특정 날짜가 포함된 달력을 생성하는 메서드
 * @param initialDate 생성할 달력의 초기 날짜
 * */
fun buildMonthGrid(
    initialDate: LocalDate,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): MonthGrid {
    val month = YearMonth.from(initialDate)
    val firstOfMonth = initialDate.withDayOfMonth(1)

    val offset = daysFromStartOfWeek(firstOfMonth.dayOfWeek, firstDayOfWeek)
    val totalDays = month.lengthOfMonth()

    val cells = offset + totalDays
    val weeks = ((cells + 6) / 7).coerceIn(4, 6)

    val firstVisible = firstOfMonth.minusDays(offset.toLong())
    val days = List(weeks * 7) { i -> firstVisible.plusDays(i.toLong()) }

    return MonthGrid(month = month, days = days, weeks = weeks)
}

/**
 * 특정 날짜가 한 주의 시작 날짜와 얼마나 떨어져 있는지 계산하는 메서드
 * @param day 계산할 날짜
 * @param start 한 주의 시작 날짜
 * */
fun daysFromStartOfWeek(
    day: DayOfWeek,
    start: DayOfWeek,
): Int {
    val d = day.value % 7
    val s = start.value % 7
    return (d - s + 7) % 7 // 음수가 나오는 경우 보정
}

/**
 * 월, 화, 수, 목, 금, 토, 일 label의 순서를 결정하는 메서드
 * @param firstDayOfWeek 첫 번째 날짜의 요일 -> 기본값은 일요일 -> 일, 월, 화, 수, 목, 금, 토 순으로 반환
 * */
fun rotateLabels(
    labels: List<String>,
    firstDayOfWeek: DayOfWeek = DayOfWeek.SUNDAY,
): List<String> {
    val startIndex = firstDayOfWeek.value % 7
    return labels.drop(startIndex) + labels.take(startIndex)
}
