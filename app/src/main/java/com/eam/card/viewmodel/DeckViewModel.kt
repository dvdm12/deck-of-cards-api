package com.eam.card.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.eam.card.model.deck.CardDto
import com.eam.card.model.deck.DeckResponse
import com.eam.card.repository.DeckRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for managing Deck of Cards operations.
 * Handles deck creation and card drawing separately.
 */
class DeckViewModel : ViewModel() {

    private var repository: DeckRepository? = null

    // --- UI STATE ---
    private val _deckState = MutableStateFlow<DeckResponse?>(null)
    val deckState: StateFlow<DeckResponse?> = _deckState

    private val _cardsState = MutableStateFlow<List<CardDto>>(emptyList())
    val cardsState: StateFlow<List<CardDto>> = _cardsState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage


    /** Initializes the repository only once per ViewModel. */
    private fun ensureRepositoryInitialized(context: Context) {
        if (repository == null) {
            repository = DeckRepository(context)
            Log.d("DeckViewModel", "Repository initialized")
        }
    }

    /**
     * Creates a new shuffled deck.
     */
    fun createNewDeck(context: Context) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("DeckViewModel", "Creating new deck...")
                val response = repository!!.createNewDeck()

                if (response?.isSuccessful == true) {
                    _deckState.value = response.body()
                    Log.d("DeckViewModel", "Deck created: ${response.body()?.deckId}")
                } else {
                    _errorMessage.value = "Error creating deck"
                    Log.e("DeckViewModel", "Deck creation failed")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                Log.e("DeckViewModel", "Exception creating deck", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Draws cards from an existing deck using its ID.
     */
    fun drawCards(context: Context, deckId: String, count: Int = 2) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("DeckViewModel", "Drawing $count cards from deck $deckId...")
                val response = repository!!.drawCards(deckId, count)

                if (response?.isSuccessful == true) {
                    val cards = response.body()?.cards ?: emptyList()
                    _cardsState.value = cards
                    Log.d("DeckViewModel", "Drew ${cards.size} cards")
                } else {
                    _errorMessage.value = "Error drawing cards"
                    Log.e("DeckViewModel", "Card draw failed")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                Log.e("DeckViewModel", "Exception drawing cards", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Adds additional cards to the current list (used for consecutive draws).
     */
    fun drawMoreCards(context: Context, deckId: String, count: Int = 2) {
        ensureRepositoryInitialized(context)
        _isLoading.value = true

        viewModelScope.launch {
            try {
                Log.d("DeckViewModel", "Drawing $count more cards from $deckId...")
                val response = repository!!.drawCards(deckId, count)

                if (response?.isSuccessful == true) {
                    val newCards = response.body()?.cards ?: emptyList()
                    _cardsState.value += newCards
                    Log.d("DeckViewModel", "Now holding ${_cardsState.value.size} cards")
                } else {
                    _errorMessage.value = "Error drawing more cards"
                    Log.e("DeckViewModel", "Draw more failed")
                }

            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                Log.e("DeckViewModel", "Exception drawing more cards", e)
            } finally {
                _isLoading.value = false
            }
        }
    }
}
