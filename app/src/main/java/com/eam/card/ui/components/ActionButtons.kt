package com.eam.card.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.eam.card.viewmodel.DeckViewModel

@Composable
fun ActionButtons(viewModel: DeckViewModel, context: android.content.Context, deckId: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(onClick = { viewModel.createNewDeck(context) }) {
            Text("Create New Deck")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { deckId?.let { viewModel.drawCards(context, it, count = 5) } },
            enabled = deckId != null
        ) {
            Text("Draw Cards")
        }
    }
}
