package com.eam.card.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.eam.card.model.deck.CardDto
import com.eam.card.model.deck.DeckResponse
import com.eam.card.repository.DeckRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * DeckViewModel
 *
 * This ViewModel manages all logic related to the Deck of Cards API.
 * It is responsible for:
 *  - Creating and shuffling new decks
 *  - Drawing and appending cards
 *  - Handling loading and error states
 *  - Providing observable StateFlows for reactive UI updates
 *
 * Supports dependency injection for repository, dispatcher, and coroutine scope,
 * which allows deterministic unit testing.
 */
class DeckViewModel(
    private var repository: DeckRepository? = null,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
) : ViewModel() {

    /** Holds the current deck information (deckId, remaining cards, etc.). */
    private val _deckState = MutableStateFlow<DeckResponse?>(null)
    val deckState: StateFlow<DeckResponse?> = _deckState

    /** Stores the list of drawn cards. */
    private val _cardsState = MutableStateFlow<List<CardDto>>(emptyList())
    val cardsState: StateFlow<List<CardDto>> = _cardsState

    /** Indicates whether an operation is currently running. */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    /** Contains the most recent error message, if any. */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    /**
     * Ensures the repository instance exists.
     * Initializes it with the given context if it was not provided.
     */
    private fun ensureRepositoryInitialized(context: Context?) {
        if (repository != null) return

        if (context != null) {
            repository = DeckRepository(context)
            Log.d("DeckViewModel", "Repository initialized with provided context.")
        } else {
            Log.e("DeckViewModel", "Repository is null and no context provided.")
            throw IllegalStateException("Repository not initialized and no context available.")
        }
    }


    /**
     * Creates a new shuffled deck.
     *
     * - Updates the loading state.
     * - Calls the repository to create a deck.
     * - Updates [deckState] or [errorMessage] accordingly.
     *
     * @param context Context required to initialize the repository if needed.
     */
    fun createNewDeck(context: Context?) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true
        _errorMessage.value = null

        scope.launch(dispatcher) {
            try {
                Log.d("DeckViewModel", "Requesting new deck creation...")
                val response = repository!!.createNewDeck()

                if (response?.isSuccessful == true) {
                    val deck = response.body()
                    if (deck != null) {
                        _deckState.value = deck
                        Log.d("DeckViewModel", "Deck created successfully: ${deck.deckId}")
                    } else {
                        handleError("Empty deck response received.")
                    }
                } else {
                    handleError("Error creating deck: ${response?.message()}")
                }
            } catch (e: Exception) {
                handleException("Exception creating deck", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Draws a specific number of cards from a given deck.
     *
     * - Updates the loading state.
     * - Calls the repository to draw cards.
     * - Updates [cardsState] or [errorMessage].
     *
     * @param context Context used to initialize the repository if needed.
     * @param deckId Identifier of the deck from which cards are drawn.
     * @param count Number of cards to draw (default = 2).
     */
    fun drawCards(context: Context, deckId: String, count: Int = 2) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true
        _errorMessage.value = null

        scope.launch(dispatcher) {
            try {
                Log.d("DeckViewModel", "Drawing $count cards from deck $deckId...")
                val response = repository!!.drawCards(deckId, count)

                if (response?.isSuccessful == true) {
                    val cards = response.body()?.cards ?: emptyList()
                    _cardsState.value = cards
                    Log.d("DeckViewModel", "Successfully drew ${cards.size} cards.")
                } else {
                    handleError("Error drawing cards: ${response?.message()}")
                }
            } catch (e: Exception) {
                handleException("Exception drawing cards", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Draws more cards from the same deck and appends them to the existing list.
     *
     * @param context Context used to initialize the repository if needed.
     * @param deckId Identifier of the deck.
     * @param count Number of additional cards to draw (default = 2).
     */
    fun drawMoreCards(context: Context, deckId: String, count: Int = 2) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true

        scope.launch(dispatcher) {
            try {
                Log.d("DeckViewModel", "Drawing $count additional cards from $deckId...")
                val response = repository!!.drawCards(deckId, count)

                if (response?.isSuccessful == true) {
                    val newCards = response.body()?.cards ?: emptyList()
                    _cardsState.value += newCards
                    Log.d("DeckViewModel", "Now holding ${_cardsState.value.size} total cards.")
                } else {
                    handleError("Error drawing more cards: ${response?.message()}")
                }
            } catch (e: Exception) {
                handleException("Exception drawing more cards", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Logs and sets an error message in [errorMessage].
     *
     * @param message Error description to display in UI.
     */
    private fun handleError(message: String) {
        _errorMessage.value = message
        Log.e("DeckViewModel", message)
    }

    /**
     * Logs an exception and updates [errorMessage].
     *
     * @param message Contextual message about where the error occurred.
     * @param e Exception instance to report.
     */
    private fun handleException(message: String, e: Exception) {
        _errorMessage.value = "Exception: ${e.message}"
        Log.e("DeckViewModel", message, e)
    }
}
