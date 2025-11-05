package com.eam.card.model.deck

import com.google.gson.annotations.SerializedName

data class DeckResponse(
    val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    val shuffled: Boolean,
    val remaining: Int
)
