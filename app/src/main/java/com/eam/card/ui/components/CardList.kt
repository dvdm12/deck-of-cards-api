package com.eam.card.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eam.card.model.deck.CardDto

@Composable
fun CardList(cards: List<CardDto>, onDrawMore: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(
            text = "Drawn Cards:",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(cards) { card ->
                CardItem(card)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onDrawMore,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Draw More Cards")
        }
    }
}
