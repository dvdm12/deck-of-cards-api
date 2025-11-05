package com.eam.card.interfaces

import com.eam.card.model.deck.DeckResponse
import com.eam.card.model.deck.DrawResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeckInterface {
    @GET("new/shuffle/")
    suspend fun shuffleDeck(
        @Query("deck_count") deckCount: Int = 1
    ): Response<DeckResponse>

    @GET("{deck_id}/draw/")
    suspend fun drawCards(
        @Path("deck_id") deckId: String,
        @Query("count") count: Int = 5
    ): Response<DrawResponse>
}