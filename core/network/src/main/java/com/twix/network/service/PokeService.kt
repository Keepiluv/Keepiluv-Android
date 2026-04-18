package com.twix.network.service

import com.twix.network.model.response.poke.model.PokeResponse
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Path

interface PokeService {
    @POST("api/v1/pokes/goals/{goalId}")
    suspend fun pokeGoal(
        @Path("goalId") goalId: Long,
    ): PokeResponse
}
