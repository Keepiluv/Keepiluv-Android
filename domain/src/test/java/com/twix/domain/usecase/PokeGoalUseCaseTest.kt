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
            fakePokeRepository.pokeGoalResult = AppResult.Success(PokeResult(message = "찌르기를 보냈습니다."))
            fakePokeRepository.pokeHistory[goalId] = null

            // when
            val result = useCase.invoke(goalId)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.Success::class.java)
            assertThat((result as PokeGoalResult.Success).message).isEqualTo("찌르기를 보냈습니다.")
            assertThat(fakePokeRepository.savedPokeHistory[goalId]).isNotNull()
        }

    @Test
    fun `쿨타임이 없고 찌르기가 실패하면 Error를 반환하고 히스토리가 저장되지 않는다`() =
        runTest {
            // given
            val goalId = 2L
            fakePokeRepository.pokeGoalResult = AppResult.Error(AppError.Network())
            fakePokeRepository.pokeHistory[goalId] = null

            // when
            val result = useCase.invoke(goalId)

            // then
            assertThat(result).isEqualTo(PokeGoalResult.Error)
            assertThat(fakePokeRepository.savedPokeHistory[goalId]).isNull()
        }

    @Test
    fun `쿨타임 중이면 OnCooldown을 반환하고 pokeGoal이 호출되지 않는다`() =
        runTest {
            // given
            val goalId = 3L
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[goalId] = recentPokedAt

            // when
            val result = useCase.invoke(goalId)

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
            val justExpiredPokedAt = System.currentTimeMillis() - PokeGoalUseCase.COOLDOWN_MS - 1
            fakePokeRepository.pokeHistory[goalId] = justExpiredPokedAt
            fakePokeRepository.pokeGoalResult = AppResult.Success(PokeResult(message = "찌르기를 보냈습니다."))

            // when
            val result = useCase.invoke(goalId)

            // then
            assertThat(result).isInstanceOf(PokeGoalResult.Success::class.java)
            assertThat((result as PokeGoalResult.Success).message).isEqualTo("찌르기를 보냈습니다.")
        }

    @Test
    fun `히스토리가 없으면 remainingCooldown은 0을 반환한다`() =
        runTest {
            // given
            val goalId = 10L
            fakePokeRepository.pokeHistory[goalId] = null

            // when
            val remaining = useCase.remainingCooldown(goalId)

            // then
            assertThat(remaining).isEqualTo(0L)
        }

    @Test
    fun `쿨타임 중이면 remainingCooldown은 양수를 반환한다`() =
        runTest {
            // given
            val goalId = 11L
            val recentPokedAt = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            fakePokeRepository.pokeHistory[goalId] = recentPokedAt

            // when
            val remaining = useCase.remainingCooldown(goalId)

            // then
            assertThat(remaining).isGreaterThan(0L)
        }

    @Test
    fun `쿨타임이 만료되었으면 remainingCooldown은 0을 반환한다`() =
        runTest {
            // given
            val goalId = 12L
            val expiredPokedAt = System.currentTimeMillis() - PokeGoalUseCase.COOLDOWN_MS - 100
            fakePokeRepository.pokeHistory[goalId] = expiredPokedAt

            // when
            val remaining = useCase.remainingCooldown(goalId)

            // then
            assertThat(remaining).isEqualTo(0L)
        }
}
