package com.eam.card.model.deck

data class CardDto(
    val code: String,
    val image: String,
    val images: CardImagesDto,
    val value: String,
    val suit: String
)
