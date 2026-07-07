package com.twix.photolog.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.model.photolog.PhotoLogs
import com.twix.domain.model.poke.PokeGoalResult
import com.twix.domain.repository.PhotoLogRepository
import com.twix.domain.usecase.PokeGoalUseCase
import com.twix.navigation.NavRoutes
import com.twix.photolog.detail.contract.PhotologDetailIntent
import com.twix.photolog.detail.contract.PhotologDetailSideEffect
import com.twix.photolog.detail.contract.PhotologDetailUiState
import com.twix.photolog.detail.contract.toUiState
import com.twix.result.AppResult
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate

class PhotologDetailViewModel(
    private val photologRepository: PhotoLogRepository,
    private val pokeGoalUseCase: PokeGoalUseCase,
    private val detailRefreshBus: PhotologRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<PhotologDetailUiState, PhotologDetailIntent, PhotologDetailSideEffect>(
        PhotologDetailUiState(),
    ) {
    private val argGoalId: Long =
        savedStateHandle[NavRoutes.PhotologDetailRoute.ARG_GOAL_ID]
            ?: error(GOAL_ID_NOT_FOUND)

    private val argTargetDate: LocalDate =
        LocalDate.parse(
            savedStateHandle[NavRoutes.PhotologDetailRoute.ARG_DATE]
                ?: error(TARGET_DATE_NOT_FOUND),
        )

    private val argBetweenUs: String =
        savedStateHandle[NavRoutes.PhotologDetailRoute.ARG_BETWEEN_US]
            ?: error(BETWEEN_US_NOT_FOUND)

    private val argIsCompleted: Boolean =
        savedStateHandle[NavRoutes.PhotologDetailRoute.ARG_IS_COMPLETED] ?: false

    private var lastReaction: GoalReactionType? = null
    private var pokeCooldownJob: Job? = null

    private val reactionFlow =
        MutableSharedFlow<GoalReactionType>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    init {
        fetchPhotolog()
        collectReactionFlow()
        collectEventBus()
        checkPokeCooldown()
    }

    private fun fetchPhotolog() {
        launchResult(
            block = ::fetchPhotologs,
            onSuccess = ::handleFetchPhotologSuccess,
            onError = { handleFetchPhotologError() },
        )
    }

    private suspend fun fetchPhotologs(): AppResult<PhotoLogs> = photologRepository.fetchPhotologs(argTargetDate, argGoalId)

    private fun handleFetchPhotologSuccess(photoLogs: PhotoLogs) {
        reduce {
            photoLogs
                .toUiState(
                    argGoalId,
                    argBetweenUs,
                    argTargetDate,
                    argIsCompleted,
                ).copy(
                    hasShownMyReaction = currentState.hasShownMyReaction,
                    pokeCooldownRemaining = currentState.pokeCooldownRemaining,
                )
        }
    }

    private suspend fun handleFetchPhotologError() {
        if (!currentState.hasLoadedContent) return
        showToast(R.string.toast_photolog_detail_fetch_fail)
    }

    @OptIn(FlowPreview::class)
    private fun collectReactionFlow() {
        viewModelScope.launch {
            reactionFlow
                .distinctUntilChanged()
                .debounce(DEBOUNCE_INTERVAL)
                .collectLatest { reaction ->
                    reactToPhotolog(reaction)
                    goalRefreshBus.notifyGoalListChanged()
                }
        }
    }

    private fun reactToPhotolog(reaction: GoalReactionType) {
        val photologId = currentState.partnerPhotolog?.photologId ?: return

        launchResult(
            block = { photologRepository.reactToPhotolog(photologId, reaction) },
            onSuccess = {},
            onError = {
                rollbackReaction()
                showToast(R.string.toast_reaction_fail)
            },
        )
    }

    private fun rollbackReaction() {
        lastReaction?.let { prev ->
            reduce { currentState.copy(partnerPhotolog = partnerPhotolog?.updateReaction(prev)) }
        }
    }

    private fun collectEventBus() {
        viewModelScope.launch {
            detailRefreshBus.events.collect { publisher ->
                when (publisher) {
                    PhotologRefreshBus.Publisher.PHOTOLOG -> {
                        fetchPhotolog()
                        goalRefreshBus.notifyGoalListChanged()
                    }

                    PhotologRefreshBus.Publisher.EDITOR -> fetchPhotolog()
                }
            }
        }
    }

    override suspend fun handleIntent(intent: PhotologDetailIntent) {
        when (intent) {
            PhotologDetailIntent.Retry -> fetchPhotolog()
            is PhotologDetailIntent.Reaction -> reduceReaction(intent.type)
            PhotologDetailIntent.Poke -> pokeToPartner()
            PhotologDetailIntent.SwipeCard -> reduceShownCard()
            PhotologDetailIntent.MyReactionEffected -> reduceMyReactionShown()
        }
    }

    private fun reduceReaction(reaction: GoalReactionType) {
        lastReaction = currentState.partnerPhotolog?.reaction
        reduce { currentState.copy(partnerPhotolog = partnerPhotolog?.updateReaction(reaction)) }
        reactionFlow.tryEmit(reaction)
    }

    private fun checkPokeCooldown() {
        viewModelScope.launch {
            val remaining = pokeGoalUseCase.remainingCooldown(argGoalId, argTargetDate.toString())
            startPokeCooldown(remaining)
        }
    }

    private fun pokeToPartner() {
        if (currentState.isPokeDisabled) return

        viewModelScope.launch {
            reduce { copy(isPoking = true) }
            handlePokeResult(pokeGoalUseCase.invoke(argGoalId, argTargetDate.toString()))
        }
    }

    private suspend fun handlePokeResult(result: PokeGoalResult) {
        when (result) {
            is PokeGoalResult.Success -> handlePokeSuccess()
            is PokeGoalResult.OnCooldown -> handlePokeCooldown(result.remainingMs)
            PokeGoalResult.Error -> handlePokeError()
        }
    }

    private fun handlePokeSuccess() {
        startPokeCooldown(PokeGoalUseCase.COOLDOWN_MS)
        reduce { copy(isPoking = false) }
        tryEmitSideEffect(PhotologDetailSideEffect.ShowPokeToast)
    }

    private fun handlePokeCooldown(remainingMs: Long) {
        startPokeCooldown(remainingMs)
        reduce { copy(isPoking = false) }
        tryEmitSideEffect(PhotologDetailSideEffect.ShowPokeCooldownToast(remainingMs))
    }

    private fun startPokeCooldown(remainingMs: Long) {
        pokeCooldownJob?.cancel()

        if (remainingMs <= 0L) {
            clearPokeCooldown()
            return
        }

        reduce { copy(pokeCooldownRemaining = remainingMs) }
        schedulePokeCooldownClear(remainingMs)
    }

    private fun clearPokeCooldown() {
        reduce { copy(pokeCooldownRemaining = 0L) }
        pokeCooldownJob = null
    }

    private fun schedulePokeCooldownClear(remainingMs: Long) {
        pokeCooldownJob =
            viewModelScope.launch {
                delay(remainingMs)
                clearPokeCooldown()
            }
    }

    private suspend fun handlePokeError() {
        reduce { copy(isPoking = false) }
        showToast(R.string.toast_poke_goal_failed)
    }

    private fun reduceShownCard() {
        reduce { toggleBetweenUs() }
    }

    private fun toggleBetweenUs(): PhotologDetailUiState =
        currentState.copy(
            currentShow =
                when (currentState.currentShow) {
                    BetweenUs.ME -> BetweenUs.PARTNER
                    BetweenUs.PARTNER -> BetweenUs.ME
                },
        )

    private fun reduceMyReactionShown() {
        reduce { copy(hasShownMyReaction = true) }
    }

    private suspend fun showToast(message: Int) {
        emitSideEffect(
            PhotologDetailSideEffect.ShowToast(
                message,
                ToastType.ERROR,
            ),
        )
    }

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val TARGET_DATE_NOT_FOUND = "Target Date Argument Not Found"
        private const val BETWEEN_US_NOT_FOUND = "Between Us Argument Not Found"
        private const val DEBOUNCE_INTERVAL = 600L
    }
}
