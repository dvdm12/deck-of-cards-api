package com.eam.card.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eam.card.ui.components.*
import com.eam.card.viewmodel.DeckViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckGameScreen() {
    val context = LocalContext.current
    val deckViewModel: DeckViewModel = viewModel()

    val deckState by deckViewModel.deckState.collectAsState()
    val cards by deckViewModel.cardsState.collectAsState()
    val isLoading by deckViewModel.isLoading.collectAsState()
    val errorMessage by deckViewModel.errorMessage.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Deck of Cards") }) },
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                when {
                    isLoading -> LoadingView()
                    errorMessage != null -> ErrorView(errorMessage!!)
                    cards.isNotEmpty() -> CardList(cards, onDrawMore = {
                        deckState?.deckId?.let { id ->
                            deckViewModel.drawMoreCards(context, id, count = 2)
                        }
                    })
                    else -> ActionButtons(deckViewModel, context, deckState?.deckId)
                }
            }
        }
    )
}
