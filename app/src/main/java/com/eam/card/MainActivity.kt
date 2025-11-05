package com.eam.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.eam.card.model.deck.CardDto
import com.eam.card.ui.theme.BreweryTheme
import com.eam.card.viewmodel.DeckViewModel

// -----------------------------------------------------------------------------
// MAIN ACTIVITY
// -----------------------------------------------------------------------------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreweryTheme {
                DeckGameScreen()
            }
        }
    }
}

// -----------------------------------------------------------------------------
// MAIN SCREEN - handles full deck lifecycle
// -----------------------------------------------------------------------------
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

// -----------------------------------------------------------------------------
// ACTION BUTTONS - create deck / draw cards
// -----------------------------------------------------------------------------
@Composable
fun ActionButtons(
    viewModel: DeckViewModel,
    context: android.content.Context,
    deckId: String?
) {
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

// -----------------------------------------------------------------------------
// CARD LIST - horizontal list of drawn cards
// -----------------------------------------------------------------------------
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

// -----------------------------------------------------------------------------
// CARD ITEM - shows individual card image and text
// -----------------------------------------------------------------------------
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

// -----------------------------------------------------------------------------
// LOADING / ERROR VIEWS
// -----------------------------------------------------------------------------
@Composable
fun LoadingView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorView(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Error: $message",
            color = MaterialTheme.colorScheme.error
        )
    }
}
