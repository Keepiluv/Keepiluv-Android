package com.twix.photolog.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.R
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.domain.repository.PokeRepository
import com.twix.navigation.NavRoutes
import com.twix.photolog.detail.contract.PhotologDetailIntent
import com.twix.photolog.detail.contract.PhotologDetailSideEffect
import com.twix.photolog.detail.contract.PhotologDetailUiState
import com.twix.photolog.detail.contract.toUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.PhotologRefreshBus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import java.time.LocalDate

class PhotologDetailViewModel(
    private val photologRepository: PhotoLogRepository,
    private val pokeRepository: PokeRepository,
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

    private val reactionFlow =
        MutableSharedFlow<GoalReactionType>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )

    init {
        fetchPhotolog()
        collectReactionFlow()
        collectEventBus()
    }

    private fun fetchPhotolog() {
        launchResult(
            block = { photologRepository.fetchPhotologs(argTargetDate, argGoalId) },
            onSuccess = {
                reduce {
                    it.toUiState(
                        argGoalId,
                        argBetweenUs,
                        argTargetDate,
                        argIsCompleted,
                    )
                }
            },
            onError = {
                showToast(R.string.photolog_detail_fetch_photolog_fail, ToastType.ERROR)
            },
            onFinally = { reduce { copy(isLoading = true) } },
        )
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
                showToast(R.string.photolog_detail_reaction_fail, ToastType.ERROR)
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

    private fun pokeToPartner() {
        launchResult(
            block = { pokeRepository.pokeGoal(argGoalId) },
            onSuccess = { tryEmitSideEffect(PhotologDetailSideEffect.ShowPokeToast(it.message)) },
            onError = { showToast(R.string.toast_poke_goal_failed, ToastType.ERROR) },
        )
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

    private suspend fun showToast(
        message: Int,
        type: ToastType,
    ) {
        emitSideEffect(PhotologDetailSideEffect.ShowToast(message, type))
    }

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val TARGET_DATE_NOT_FOUND = "Target Date Argument Not Found"
        private const val BETWEEN_US_NOT_FOUND = "Between Us Argument Not Found"
        private const val DEBOUNCE_INTERVAL = 600L
    }
}
