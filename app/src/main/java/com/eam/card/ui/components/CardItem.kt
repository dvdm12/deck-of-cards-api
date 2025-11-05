package com.eam.card.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.eam.card.model.deck.CardDto

@Composable
fun CardItem(card: CardDto) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .height(220.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = card.image,
            contentDescription = "${card.value} of ${card.suit}",
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
        )
        Text(
            text = "${card.value} of ${card.suit}",
            modifier = Modifier.padding(8.dp)
        )
    }
}
