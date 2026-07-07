package com.twix.domain.usecase

import com.twix.domain.model.poke.PokeGoalResult
import com.twix.domain.repository.PokeRepository
import com.twix.result.AppResult

class PokeGoalUseCase(
    private val pokeRepository: PokeRepository,
) {
    suspend fun invoke(
        goalId: Long,
        targetDate: String,
    ): PokeGoalResult {
        val remainingMs = remainingCooldown(goalId, targetDate)
        if (remainingMs > 0) return PokeGoalResult.OnCooldown(remainingMs)

        return when (val result = pokeRepository.pokeGoal(goalId)) {
            is AppResult.Success -> {
                pokeRepository.savePokeHistory(goalId, targetDate, System.currentTimeMillis())
                PokeGoalResult.Success(result.data.message)
            }
            is AppResult.Error -> PokeGoalResult.Error
        }
    }

    suspend fun remainingCooldown(
        goalId: Long,
        targetDate: String,
    ): Long {
        val pokedAt = pokeRepository.findPokeHistory(goalId, targetDate) ?: return 0L
        val currentTime = System.currentTimeMillis()
        val elapsedMs = currentTime - pokedAt
        val remainingMs = COOLDOWN_MS - elapsedMs

        return remainingMs.coerceAtLeast(0L)
    }

    companion object {
        const val COOLDOWN_MS = 3 * 60 * 60 * 1000L
    }
}
