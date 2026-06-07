package com.twix.photolog.detail

import androidx.lifecycle.SavedStateHandle
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalIconType
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photo.PhotoLogUploadInfo
import com.twix.domain.model.photo.PhotologParam
import com.twix.domain.model.photolog.GoalPhotolog
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.model.poke.PokeResult
import com.twix.domain.repository.PhotoLogRepository
import com.twix.domain.repository.PokeRepository
import com.twix.domain.usecase.PokeGoalUseCase
import com.twix.navigation.NavRoutes
import com.twix.photolog.detail.contract.PhotologDetailIntent
import com.twix.result.AppResult
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class PhotologDetailViewModelTest {
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `쿨타임이 만료되면 찌르기 비활성 상태가 해제된다`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            val goalId = 170L
            val remainingMs = 1_000L
            val pokeRepository =
                FakePokeRepository().apply {
                    pokeHistory[goalId] = System.currentTimeMillis() - PokeGoalUseCase.COOLDOWN_MS + remainingMs
                }
            val viewModel = createViewModel(goalId = goalId, pokeRepository = pokeRepository)

            runCurrent()
            assertThat(viewModel.uiState.value.pokeCooldownRemaining).isGreaterThan(0L)
            assertThat(viewModel.uiState.value.isPokeDisabled).isTrue()

            advanceTimeBy(remainingMs + 1L)
            advanceUntilIdle()

            assertThat(viewModel.uiState.value.pokeCooldownRemaining).isEqualTo(0L)
            assertThat(viewModel.uiState.value.isPokeDisabled).isFalse()
        }

    @Test
    fun `찌르기 결과가 쿨타임이면 최신 잔여 시간을 상태에 반영한다`() =
        runTest {
            val dispatcher = StandardTestDispatcher(testScheduler)
            Dispatchers.setMain(dispatcher)
            val goalId = 171L
            val pokeRepository = FakePokeRepository()
            val viewModel = createViewModel(goalId = goalId, pokeRepository = pokeRepository)
            advanceUntilIdle()
            assertThat(viewModel.uiState.value.pokeCooldownRemaining).isEqualTo(0L)

            pokeRepository.pokeHistory[goalId] = System.currentTimeMillis() - (PokeGoalUseCase.COOLDOWN_MS / 2)
            viewModel.dispatch(PhotologDetailIntent.Poke)
            runCurrent()

            assertThat(viewModel.uiState.value.isPoking).isFalse()
            assertThat(viewModel.uiState.value.pokeCooldownRemaining).isGreaterThan(0L)
            assertThat(viewModel.uiState.value.isPokeDisabled).isTrue()
            assertThat(pokeRepository.pokeGoalCallCount).isEqualTo(0)
        }

    private fun createViewModel(
        goalId: Long,
        pokeRepository: FakePokeRepository,
        targetDate: LocalDate = LocalDate.of(2026, 6, 6),
    ): PhotologDetailViewModel =
        PhotologDetailViewModel(
            photologRepository = FakePhotoLogRepository(goalId, targetDate),
            pokeGoalUseCase = PokeGoalUseCase(pokeRepository),
            detailRefreshBus = PhotologRefreshBus(),
            goalRefreshBus = GoalRefreshBus(),
            savedStateHandle =
                SavedStateHandle(
                    mapOf(
                        NavRoutes.PhotologDetailRoute.ARG_GOAL_ID to goalId,
                        NavRoutes.PhotologDetailRoute.ARG_DATE to targetDate.toString(),
                        NavRoutes.PhotologDetailRoute.ARG_BETWEEN_US to BetweenUs.PARTNER.name,
                        NavRoutes.PhotologDetailRoute.ARG_IS_COMPLETED to false,
                    ),
                ),
        )
}

private class FakePokeRepository : PokeRepository {
    val pokeHistory = mutableMapOf<Long, Long?>()
    var pokeGoalCallCount = 0
    var pokeGoalResult: AppResult<PokeResult> = AppResult.Success(PokeResult(message = "ok"))

    override suspend fun pokeGoal(goalId: Long): AppResult<PokeResult> {
        pokeGoalCallCount += 1
        return pokeGoalResult
    }

    override suspend fun savePokeHistory(
        goalId: Long,
        pokedAt: Long,
    ) {
        pokeHistory[goalId] = pokedAt
    }

    override suspend fun findPokeHistory(goalId: Long): Long? = pokeHistory[goalId]
}

private class FakePhotoLogRepository(
    private val goalId: Long,
    private val targetDate: LocalDate,
) : PhotoLogRepository {
    override suspend fun getUploadUrl(goalId: Long): AppResult<PhotoLogUploadInfo> = error("Not used")

    override suspend fun uploadPhotolog(photologParam: PhotologParam): AppResult<Unit> = error("Not used")

    override suspend fun uploadPhotologImage(
        goalId: Long,
        bytes: ByteArray,
        contentType: String,
    ): AppResult<String> = error("Not used")

    override suspend fun fetchPhotologs(
        targetDate: LocalDate,
        goalId: Long?,
    ): AppResult<PhotoLogs> =
        AppResult.Success(
            PhotoLogs(
                targetDate = this.targetDate.toString(),
                myNickname = "me",
                partnerNickname = "partner",
                goals =
                    listOf(
                        GoalPhotolog(
                            goalId = this.goalId,
                            goalName = "goal",
                            icon = GoalIconType.DEFAULT,
                            myPhotolog = null,
                            partnerPhotolog = null,
                        ),
                    ),
            ),
        )

    override suspend fun reactToPhotolog(
        photologId: Long,
        reaction: GoalReactionType,
    ): AppResult<Unit> = error("Not used")

    override suspend fun modifyPhotolog(
        photologId: Long,
        fileName: String,
        comment: String,
    ): AppResult<Unit> = error("Not used")
}
