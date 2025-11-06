package com.eam.card.ui.screen

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.annotation.ExperimentalCoilApi
import com.eam.card.R
import com.eam.card.ui.components.*
import com.eam.card.ui.theme.BreweryTheme
import com.eam.card.viewmodel.DeckViewModel

/**
 * DeckGameScreen
 * ---------------------------------------------------------------
 * Main screen for the Deck of Cards demo.
 * - Manages state via DeckViewModel
 * - Displays animated background and transitions
 * - Handles loading, error, empty, and content states
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckGameScreen() {
    val context = LocalContext.current
    val deckViewModel = remember { DeckViewModel() }

    // Reactive state collection
    val deckState by deckViewModel.deckState.collectAsState()
    val cards by deckViewModel.cardsState.collectAsState()
    val isLoading by deckViewModel.isLoading.collectAsState()
    val errorMessage by deckViewModel.errorMessage.collectAsState()

    // Infinite animation for dynamic background
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(25000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    // Determine UI state
    val currentState: ContentState = when {
        isLoading -> ContentState.Loading
        errorMessage != null -> ContentState.Error
        cards.isNotEmpty() -> ContentState.Cards
        else -> ContentState.Empty
    }

    Scaffold(
        topBar = {
            ModernTopBar(
                cardCount = cards.size,
                remainingCards = deckState?.remaining ?: 0
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            Color(0xFF1E293B),
                            Color(0xFF334155)
                        ),
                        start = androidx.compose.ui.geometry.Offset(animatedOffset, 0f),
                        end = androidx.compose.ui.geometry.Offset(animatedOffset + 800f, 1000f)
                    )
                )
        ) {
            AnimatedBackground()

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
            ) {
                // -------------------------------
                // OPTION 1 — Simplified Crossfade
                // -------------------------------
                Crossfade(targetState = currentState, label = "content") { target ->
                    when (target) {
                        ContentState.Loading -> LoadingView()
                        ContentState.Error -> errorMessage?.let { ErrorView(it) }
                        ContentState.Cards -> CardList(
                            cards = cards,
                            onDrawMore = {
                                deckState?.deckId?.let { id ->
                                    deckViewModel.drawMoreCards(context, id, count = 2)
                                }
                            }
                        )
                        ContentState.Empty -> ActionButtons(deckViewModel, context, deckState?.deckId)
                    }
                }

                // -------------------------------
                // OPTION 2 — Advanced Animation
                // Uncomment if you prefer AnimatedContent
                // -------------------------------
                /*
                AnimatedContent<ContentState>(
                    targetState = currentState,
                    transitionSpec = {
                        (fadeIn(tween(400)) + scaleIn(initialScale = 0.9f, tween(400))) togetherWith
                                (fadeOut(tween(200)) + scaleOut(targetScale = 0.9f, tween(200)))
                    },
                    label = "content"
                ) { target ->
                    when (target) {
                        ContentState.Loading -> LoadingView()
                        ContentState.Error -> errorMessage?.let { ErrorView(it) }
                        ContentState.Cards -> CardList(
                            cards = cards,
                            onDrawMore = {
                                deckState?.deckId?.let { id ->
                                    deckViewModel.drawMoreCards(context, id, count = 2)
                                }
                            }
                        )
                        ContentState.Empty -> ActionButtons(deckViewModel, context, deckState?.deckId)
                    }
                }
                */
            }
        }
    }
}

/**
 * ModernTopBar — Displays app title, icon, and stats.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTopBar(cardCount: Int, remainingCards: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = Color(0xFF1E293B).copy(alpha = 0.95f),
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFF6366F1).copy(alpha = 0.2f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_poker_svgrepo_com),
                        contentDescription = stringResource(R.string.poker_casino_icon),
                        tint = Color.White,
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Deck of Cards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (cardCount > 0) {
                        Text(
                            text = "$cardCount cards drawn",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            if (remainingCards > 0) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFF8B5CF6).copy(alpha = 0.2f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "$remainingCards",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8B5CF6)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "left",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * AnimatedBackground — Decorative blurred gradients for atmosphere.
 */
@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "circles")

    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale1"
    )

    val scale2 by infiniteTransition.animateFloat(
        initialValue = 1.2f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale2"
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = (-100).dp, y = 100.dp)
                .size(300.dp)
                .scale(scale1)
                .blur(80.dp)
                .background(
                    color = Color(0xFF6366F1).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                )
        )

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(350.dp)
                .scale(scale2)
                .blur(80.dp)
                .background(
                    color = Color(0xFF8B5CF6).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(50)
                )
        )
    }
}

/**
 * Enum representing screen states for Crossfade/AnimatedContent.
 */
private enum class ContentState { Loading, Error, Cards, Empty }

// -----------------------------------------------------------------------------
// PREVIEWS
// -----------------------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class, ExperimentalCoilApi::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeckGameScreenPreview() {
    BreweryTheme { DeckGameScreen() }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeckGameScreenEmptyPreview() {
    BreweryTheme {
        ModernTopBar(cardCount = 0, remainingCards = 0)
    }
}
