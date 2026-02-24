package com.twix.data.repository

import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.RepeatCycle
import com.twix.domain.model.enums.StampColor
import com.twix.domain.model.enums.StampType
import com.twix.domain.model.stats.ParticipantStats
import com.twix.domain.model.stats.Stats
import com.twix.domain.model.stats.StatsGoal
import com.twix.domain.model.stats.detail.CompletedDate
import com.twix.domain.model.stats.detail.StatsDetail
import com.twix.domain.model.stats.detail.StatsSummary
import com.twix.domain.repository.StatsRepository
import com.twix.network.execute.safeApiCall
import com.twix.result.AppResult
import java.time.LocalDate
import kotlin.random.Random

class FakeStatsRepository : StatsRepository {
    private val fakeInProgressStore = mutableMapOf<LocalDate, Stats>()
    private val fakeEndGoals = mutableListOf<StatsGoal>()

    init {
        initializeFakeData()
    }

    override suspend fun fetchStatsDetail(
        goalId: Long,
        date: LocalDate?,
    ): AppResult<StatsDetail> =
        safeApiCall {
            val isEnd = fakeEndGoals.any { it.goalId == goalId }
            val monthStart =
                when {
                    date != null -> date.withDayOfMonth(1)
                    isEnd -> LocalDate.of(2025, 9, 1)
                    else -> LocalDate.now().withDayOfMonth(1)
                }
            val today = LocalDate.now()
            val lastDay = monthStart.lengthOfMonth()

            val matchedGoal =
                fakeInProgressStore[monthStart]
                    ?.statsGoals
                    ?.find { it.goalId == goalId }
                    ?: fakeEndGoals.find { it.goalId == goalId }

            val goalName = matchedGoal?.goalName ?: "운동 인증 챌린지"
            val goalIcon = matchedGoal?.goalIconType ?: GoalIconType.DEFAULT
            val status = if (isEnd) "COMPLETED" else "IN_PROGRESS"
            val monthlyTarget = matchedGoal?.monthlyTargetCount ?: 15

            val maxDay =
                when {
                    monthStart.isAfter(today.withDayOfMonth(1)) -> 0
                    monthStart.year == today.year && monthStart.month == today.month -> today.dayOfMonth
                    else -> lastDay
                }

            val completedDates =
                (1..maxDay)
                    .filter { Random.nextBoolean() }
                    .take(monthlyTarget)
                    .map { day ->
                        CompletedDate(
                            date = monthStart.withDayOfMonth(day),
                            myImageUrl = if (Random.nextBoolean()) "https://picsum.photos/seed/${goalId}_my_$day/100" else null,
                            partnerImageUrl = if (Random.nextBoolean()) "https://picsum.photos/seed/${goalId}_partner_$day/100" else null,
                        )
                    }

            val startDate = LocalDate.of(2025, 1, 1)
            val endDate = if (isEnd) LocalDate.of(2025, 9, 30) else null

            StatsDetail(
                goalId = goalId,
                goalName = goalName,
                goalIcon = goalIcon,
                status = status,
                monthDate = monthStart,
                completedDate = completedDates,
                statsSummary =
                    StatsSummary(
                        myNickname = "찬호",
                        partnerNickname = "페토",
                        totalCount = monthlyTarget,
                        myCompletedCount = completedDates.count { it.myImageUrl != null },
                        partnerCompletedCount = completedDates.count { it.partnerImageUrl != null },
                        repeatCycle = RepeatCycle.DAILY,
                        startDate = startDate,
                        endDate = endDate,
                    ),
            )
        }

    override suspend fun fetchInProgressStats(date: LocalDate): AppResult<Stats> =
        safeApiCall {
            fakeInProgressStore[date.withDayOfMonth(1)] ?: Stats.EMPTY
        }

    override suspend fun fetchEndStats(): AppResult<List<StatsGoal>> =
        safeApiCall {
            fakeEndGoals.toList()
        }

    private fun initializeFakeData() {
        val now = LocalDate.now()
        (-2..3).forEach { monthOffset ->
            val date = now.plusMonths(monthOffset.toLong()).withDayOfMonth(1)
            fakeInProgressStore[date] = createFakeStats(date, isEmpty = monthOffset == -1)
        }

        fakeEndGoals.addAll(
            listOf(
                createFakeGoal(
                    id = 10L,
                    goalName = "줄넘기 하기",
                    iconType = GoalIconType.EXERCISE,
                    stamp = StampType.MOON,
                    monthlyTarget = 20,
                ),
                createFakeGoal(
                    id = 11L,
                    goalName = "일기 쓰기",
                    iconType = GoalIconType.PENCIL,
                    stamp = StampType.NOTE,
                    monthlyTarget = 15,
                ),
                createFakeGoal(
                    id = 12L,
                    goalName = "물 마시기",
                    iconType = GoalIconType.HEALTH,
                    stamp = StampType.FLOWER,
                    monthlyTarget = 30,
                ),
            ),
        )
    }

    private fun createFakeStats(
        date: LocalDate,
        isEmpty: Boolean = false,
    ): Stats =
        Stats(
            selectedDate = date,
            statsGoals =
                listOf(
                    createFakeGoal(
                        id = 1L,
                        goalName = "아이스크림 먹기",
                        iconType = GoalIconType.DEFAULT,
                        stamp = StampType.CLOVER,
                        monthlyTarget = 20,
                    ),
                    createFakeGoal(
                        id = 2L,
                        goalName = "운동하기",
                        iconType = GoalIconType.EXERCISE,
                        stamp = StampType.HEART,
                        monthlyTarget = 15,
                    ),
                    createFakeGoal(
                        id = 3L,
                        goalName = "독서하기",
                        iconType = GoalIconType.BOOK,
                        stamp = StampType.FLOWER,
                        monthlyTarget = 10,
                        isEmpty = isEmpty,
                    ),
                ),
        )

    private fun createFakeGoal(
        id: Long,
        goalName: String,
        iconType: GoalIconType,
        stamp: StampType,
        monthlyTarget: Int,
        isEmpty: Boolean = false,
    ): StatsGoal {
        val myCount = if (isEmpty) 0 else Random.nextInt(5, monthlyTarget)
        val partnerCount = if (isEmpty) 0 else Random.nextInt(5, monthlyTarget)

        return StatsGoal(
            goalId = id,
            goalName = goalName,
            goalIconType = iconType,
            monthlyTargetCount = if (isEmpty) 0 else monthlyTarget,
            stamp = stamp,
            myStats =
                ParticipantStats(
                    nickname = "찬호",
                    endCount = if (isEmpty) 0 else Random.nextInt(3, myCount + 1),
                    stampColors = List(myCount) { StampColor.entries.random() },
                ),
            partnerStats =
                ParticipantStats(
                    nickname = "페토",
                    endCount = if (isEmpty) 0 else Random.nextInt(3, partnerCount + 1),
                    stampColors = List(partnerCount) { StampColor.entries.random() },
                ),
        )
    }
}
