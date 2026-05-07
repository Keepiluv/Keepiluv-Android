package com.twix.domain.model.poke

sealed interface PokeGoalResult {
    data class Success(
        val message: String,
    ) : PokeGoalResult

    data class OnCooldown(
        val remainingMs: Long,
    ) : PokeGoalResult

    data object Error : PokeGoalResult
}
