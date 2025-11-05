package com.eam.card.model.deck

import com.google.gson.annotations.SerializedName

data class DrawResponse(
    val success: Boolean,
    @SerializedName("deck_id") val deckId: String,
    val cards: List<CardDto>,
    val remaining: Int
)
