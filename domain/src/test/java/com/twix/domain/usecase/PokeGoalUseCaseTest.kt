package com.twix.domain.usecase

import com.twix.domain.fake.FakePokeRepository
import com.twix.domain.model.poke.PokeGoalResult
import com.twix.domain.model.poke.PokeResult
import com.twix.result.AppError
import com.twix.result.AppResult
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PokeGoalUseCaseTest {
    private lateinit var fakePokeRepository: FakePokeRepository
    private lateinit var useCase: PokeGoalUseCase

    @BeforeEach
    fun setUp() {
        fakePokeRepository = FakePokeRepository()
        useCase = PokeGoalUseCase(fakePokeRepository)
    }

    @Test
    fun `쿨타임이 없고 찌르기가 성공하면 Success를 반환하고 히스토리가 저장된다`() =
        runTest {
            // given
            val goalId = 1L
            val targetDate = "2026-06-07"
            val serverMessage = "서버 응답 메시지"
            fakePokeRepository.pokeGoalResult = AppResult.Success(PokeResult(message = serverMessage))
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = null

            // when
            val result = useCase.invoke(goalId, targetDate)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.Success::class.java)
            assertThat((result as PokeGoalResult.Success).message).isEqualTo(serverMessage)
            assertThat(fakePokeRepository.savedPokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)])
                .isNotNull()
        }

    @Test
    fun `쿨타임이 없고 찌르기가 실패하면 Error를 반환하고 히스토리가 저장되지 않는다`() =
        runTest {
            // given
            val goalId = 2L
            val targetDate = "2026-06-07"
            fakePokeRepository.pokeGoalResult = AppResult.Error(AppError.Network())
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = null

            // when
            val result = useCase.invoke(goalId, targetDate)

            // then
            assertThat(result).isEqualTo(PokeGoalResult.Error)
            assertThat(fakePokeRepository.savedPokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)])
                .isNull()
        }

    @Test
    fun `쿨타임 중이면 OnCooldown을 반환하고 pokeGoal이 호출되지 않는다`() =
        runTest {
            // given
            val goalId = 3L
            val targetDate = "2026-06-07"
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = recentPokedAt

            // when
            val result = useCase.invoke(goalId, targetDate)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.OnCooldown::class.java)
            assertThat((result as PokeGoalResult.OnCooldown).remainingMs).isGreaterThan(0L)
            assertThat(fakePokeRepository.pokeGoalCallCount).isEqualTo(0)
        }

    @Test
    fun `쿨타임 만료 직후에는 찌르기가 성공하고 Success를 반환한다`() =
        runTest {
            // given
            val goalId = 4L
            val targetDate = "2026-06-07"
            val justExpiredPokedAt = System.currentTimeMillis() - PokeGoalUseCase.COOLDOWN_MS - 1
            val serverMessage = "서버 응답 메시지"
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = justExpiredPokedAt
            fakePokeRepository.pokeGoalResult = AppResult.Success(PokeResult(message = serverMessage))

            // when
            val result = useCase.invoke(goalId, targetDate)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.Success::class.java)
            assertThat((result as PokeGoalResult.Success).message).isEqualTo(serverMessage)
        }

    @Test
    fun `히스토리가 없으면 remainingCooldown은 0을 반환한다`() =
        runTest {
            // given
            val goalId = 10L
            val targetDate = "2026-06-07"
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = null

            // when
            val remaining = useCase.remainingCooldown(goalId, targetDate)

            // then
            assertThat(remaining).isEqualTo(0L)
        }

    @Test
    fun `쿨타임 중이면 remainingCooldown은 양수를 반환한다`() =
        runTest {
            // given
            val goalId = 11L
            val targetDate = "2026-06-07"
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = recentPokedAt

            // when
            val remaining = useCase.remainingCooldown(goalId, targetDate)

            // then
            assertThat(remaining).isGreaterThan(0L)
        }

    @Test
    fun `쿨타임이 만료되었으면 remainingCooldown은 0을 반환한다`() =
        runTest {
            // given
            val goalId = 12L
            val targetDate = "2026-06-07"
            val expiredPokedAt = System.currentTimeMillis() - PokeGoalUseCase.COOLDOWN_MS - 100
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, targetDate)] = expiredPokedAt

            // when
            val remaining = useCase.remainingCooldown(goalId, targetDate)

            // then
            assertThat(remaining).isEqualTo(0L)
        }

    @Test
    fun `같은 목표라도 날짜가 다르면 remainingCooldown은 독립적으로 계산된다`() =
        runTest {
            // given
            val goalId = 20L
            val pokedDate = "2026-06-07"
            val otherDate = "2026-06-08"
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, pokedDate)] = recentPokedAt

            // when
            val pokedDateRemaining = useCase.remainingCooldown(goalId, pokedDate)
            val otherDateRemaining = useCase.remainingCooldown(goalId, otherDate)

            // then
            assertThat(pokedDateRemaining).isGreaterThan(0L)
            assertThat(otherDateRemaining).isEqualTo(0L)
        }

    @Test
    fun `같은 목표의 다른 날짜 쿨타임은 찌르기 요청을 막지 않는다`() =
        runTest {
            // given
            val goalId = 21L
            val pokedDate = "2026-06-07"
            val otherDate = "2026-06-08"
            val serverMessage = "서버 응답 메시지"
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[FakePokeRepository.PokeHistoryKey(goalId, pokedDate)] = recentPokedAt
            fakePokeRepository.pokeGoalResult = AppResult.Success(PokeResult(message = serverMessage))

            // when
            val result = useCase.invoke(goalId, otherDate)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.Success::class.java)
            assertThat(fakePokeRepository.pokeGoalCallCount).isEqualTo(1)
            assertThat(fakePokeRepository.savedPokeHistory[FakePokeRepository.PokeHistoryKey(goalId, otherDate)])
                .isNotNull()
            assertThat(fakePokeRepository.savedPokeHistory[FakePokeRepository.PokeHistoryKey(goalId, pokedDate)])
                .isNull()
        }
}
