package com.eam.card.repository

import android.content.Context
import android.util.Log
import com.eam.card.model.deck.DeckResponse
import com.eam.card.model.deck.DrawResponse
import com.eam.card.interfaces.DeckInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

/**
 * Repository responsible for communicating with the Deck of Cards API.
 * Each operation (deck creation and draw) runs asynchronously but sequentially.
 */
class DeckRepository(context: Context) {

    private val api: DeckInterface = RetrofitClient
        .getInstance(context)
        .create(DeckInterface::class.java)

    /**
     * Creates a new shuffled deck asynchronously.
     */
    suspend fun createNewDeck(): Response<DeckResponse>? = safeApiCall {
        api.shuffleDeck()
    }

    /**
     * Draws a given number of cards from the specified deck asynchronously.
     */
    suspend fun drawCards(deckId: String, count: Int = 5): Response<DrawResponse>? = safeApiCall {
        api.drawCards(deckId, count)
    }

    /**
     * High-level operation: Creates a deck, then draws cards from it.
     * This sequence is asynchronous but logically dependent.
     */
    suspend fun createDeckThenDraw(count: Int = 2): Pair<Response<DeckResponse>?, Response<DrawResponse>?> {
        val deckResponse = createNewDeck()
        val deckId = deckResponse?.body()?.deckId ?: return Pair(deckResponse, null)

        val drawResponse = drawCards(deckId, count)
        return Pair(deckResponse, drawResponse)
    }

    /**
     * Safe API call wrapper for consistent error handling and logging.
     */
    private suspend fun <T> safeApiCall(block: suspend () -> Response<T>): Response<T>? =
        withContext(Dispatchers.IO) {
            try {
                val response = block()
                if (response.isSuccessful) {
                    Log.d("DeckRepository", "Success: ${response.raw().request.url}")
                    response
                } else {
                    Log.e("DeckRepository", "API error: ${response.code()} ${response.message()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("DeckRepository", "Exception: ${e.message}", e)
                null
            }
        }
}