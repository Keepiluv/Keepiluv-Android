package com.twix.task_certification.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.twix.designsystem.components.toast.model.ToastType
import com.twix.domain.model.enums.BetweenUs
import com.twix.domain.model.enums.GoalReactionType
import com.twix.domain.repository.PhotoLogRepository
import com.twix.navigation.NavRoutes
import com.twix.task_certification.R
import com.twix.task_certification.detail.model.TaskCertificationDetailIntent
import com.twix.task_certification.detail.model.TaskCertificationDetailSideEffect
import com.twix.task_certification.detail.model.TaskCertificationDetailUiState
import com.twix.task_certification.detail.model.toUiState
import com.twix.ui.base.BaseViewModel
import com.twix.util.bus.GoalRefreshBus
import com.twix.util.bus.TaskCertificationRefreshBus
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class TaskCertificationDetailViewModel(
    private val photologRepository: PhotoLogRepository,
    private val taskCertificationRefreshBus: TaskCertificationRefreshBus,
    private val goalRefreshBus: GoalRefreshBus,
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<TaskCertificationDetailUiState, TaskCertificationDetailIntent, TaskCertificationDetailSideEffect>(
        TaskCertificationDetailUiState(),
    ) {
    private val argGoalId: Long =
        savedStateHandle[NavRoutes.TaskCertificationDetailRoute.ARG_GOAL_ID]
            ?: error(GOAL_ID_NOT_FOUND)

    private val argTargetDate: String =
        savedStateHandle[NavRoutes.TaskCertificationDetailRoute.ARG_DATE]
            ?: error(TARGET_DATE_NOT_FOUND)

    private val argBetweenUs: String =
        savedStateHandle[NavRoutes.TaskCertificationDetailRoute.ARG_BETWEEN_US]
            ?: error(BETWEEN_US_NOT_FOUND)

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
            block = { photologRepository.fetchPhotoLogs(argTargetDate) },
            onSuccess = { reduce { it.toUiState(argGoalId, argBetweenUs) } },
            onError = {
                showToast(R.string.task_certification_detail_fetch_photolog_fail, ToastType.ERROR)
            },
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
                showToast(R.string.task_certification_detail_reaction_fail, ToastType.ERROR)
            },
        )
    }

    private fun rollbackReaction() {
        lastReaction?.let { prev ->
            reduce { currentState.copy(partnerPhotolog = partnerPhotolog?.updateReaction(prev)) }
        }
    }

    private suspend fun reduceReaction(reaction: GoalReactionType) {
        lastReaction = currentState.partnerPhotolog?.reaction
        reduce { currentState.copy(partnerPhotolog = partnerPhotolog?.updateReaction(reaction)) }
        reactionFlow.emit(reaction)
    }

    private fun collectEventBus() {
        viewModelScope.launch {
            taskCertificationRefreshBus.events.collect {
                fetchPhotolog()
                goalRefreshBus.notifyGoalListChanged()
            }
        }
    }

    override suspend fun handleIntent(intent: TaskCertificationDetailIntent) {
        when (intent) {
            is TaskCertificationDetailIntent.Reaction -> reduceReaction(intent.type)
            TaskCertificationDetailIntent.Sting -> TODO("찌르기 API 연동")
            TaskCertificationDetailIntent.SwipeCard -> reduceShownCard()
        }
    }

    private fun reduceShownCard() {
        reduce { toggleBetweenUs() }
    }

    private fun toggleBetweenUs(): TaskCertificationDetailUiState =
        currentState.copy(
            currentShow =
                when (currentState.currentShow) {
                    BetweenUs.ME -> BetweenUs.PARTNER
                    BetweenUs.PARTNER -> BetweenUs.ME
                },
        )

    private fun showToast(
        message: Int,
        type: ToastType,
    ) {
        viewModelScope.launch {
            emitSideEffect(TaskCertificationDetailSideEffect.ShowToast(message, type))
        }
    }

    companion object {
        private const val GOAL_ID_NOT_FOUND = "Goal Id Argument Not Found"
        private const val TARGET_DATE_NOT_FOUND = "Target Date Argument Not Found"
        private const val BETWEEN_US_NOT_FOUND = "Between Us Argument Not Found"
        private const val DEBOUNCE_INTERVAL = 600L
    }
}
