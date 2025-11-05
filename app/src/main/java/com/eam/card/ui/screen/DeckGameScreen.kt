package com.eam.card.ui.screen

import androidx.compose.animation.*
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eam.card.R
import com.eam.card.model.deck.CardDto
import com.eam.card.model.deck.CardImagesDto
import com.eam.card.ui.components.*
import com.eam.card.ui.theme.BreweryTheme
import com.eam.card.viewmodel.DeckViewModel

/**
 * DeckGameScreen - Main game screen with animated background and state management
 *
 * This screen manages the complete card game experience with:
 * - Animated gradient background that flows smoothly
 * - State-based content display (loading, error, cards, empty)
 * - Modern top bar with real-time statistics
 * - Smooth transitions between different states
 *
 * The screen follows Material Design 3 guidelines with custom animations
 * and a dark-themed aesthetic for an immersive gaming experience.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeckGameScreen() {
    val context = LocalContext.current
    val deckViewModel: DeckViewModel = viewModel()

    // Collect state flows from ViewModel
    val deckState by deckViewModel.deckState.collectAsState()
    val cards by deckViewModel.cardsState.collectAsState()
    val isLoading by deckViewModel.isLoading.collectAsState()
    val errorMessage by deckViewModel.errorMessage.collectAsState()

    // Infinite animation for dynamic background gradient
    val infiniteTransition = rememberInfiniteTransition(label = "background")
    val animatedOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "offset"
    )

    Scaffold(
        topBar = {
            ModernTopBar(
                cardCount = cards.size,
                remainingCards = deckState?.remaining ?: 0
            )
        },
        containerColor = Color.Transparent,
        content = { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        // Animated linear gradient creates flowing effect
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF0F172A), // Slate 900 - Deepest shade
                                Color(0xFF1E293B), // Slate 800 - Medium shade
                                Color(0xFF334155)  // Slate 700 - Lighter shade
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                animatedOffset,
                                animatedOffset
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                animatedOffset + 1000f,
                                animatedOffset + 1000f
                            )
                        )
                    )
            ) {
                // Decorative animated circles in background
                AnimatedBackground()

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxSize()
                ) {
                    // Animated content switcher with smooth transitions
                    AnimatedContent(
                        targetState = when {
                            isLoading -> ContentState.Loading
                            errorMessage != null -> ContentState.Error
                            cards.isNotEmpty() -> ContentState.Cards
                            else -> ContentState.Empty
                        },
                        transitionSpec = {
                            // Fade in with scale for smooth appearance
                            fadeIn(animationSpec = tween(400)) +
                                    scaleIn(
                                        initialScale = 0.92f,
                                        animationSpec = tween(400)
                                    ) togetherWith
                                    fadeOut(animationSpec = tween(200)) +
                                    scaleOut(
                                        targetScale = 0.92f,
                                        animationSpec = tween(200)
                                    )
                        },
                        label = "content"
                    ) { targetState ->
                        when (targetState) {
                            ContentState.Loading -> LoadingView()
                            ContentState.Error -> ErrorView(errorMessage!!)
                            ContentState.Cards -> CardList(
                                cards = cards,
                                onDrawMore = {
                                    // Draw 2 more cards when user requests
                                    deckState?.deckId?.let { id ->
                                        deckViewModel.drawMoreCards(context, id, count = 2)
                                    }
                                }
                            )
                            ContentState.Empty -> ActionButtons(
                                deckViewModel,
                                context,
                                deckState?.deckId
                            )
                        }
                    }
                }
            }
        }
    )
}

/**
 * ModernTopBar - Custom app bar with card statistics
 *
 * Displays:
 * - App icon with branded color
 * - Title with dynamic card count subtitle
 * - Remaining cards badge (only visible when deck exists)
 *
 * Features semi-transparent background with elevation for depth.
 *
 * @param cardCount Number of cards currently drawn
 * @param remainingCards Number of cards left in the deck
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
            // Left section: Icon and title
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Branded icon container with rounded corners
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

                // Title with optional subtitle showing drawn card count
                Column {
                    Text(
                        text = "Deck of Cards",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    // Only show card count when cards have been drawn
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

            // Right section: Remaining cards badge
            // Only visible when a deck exists
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
 * AnimatedBackground - Decorative background with pulsing circles
 *
 * Creates an immersive ambient effect using:
 * - Two large blurred circles
 * - Independent scale animations
 * - Semi-transparent gradient colors
 * - Strategic positioning for visual balance
 *
 * The circles use blur effect to create a glassmorphism aesthetic
 * without impacting performance significantly.
 */
@Composable
private fun AnimatedBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "circles")

    // First circle animation: grows from 1.0 to 1.3 scale
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.3f,
        animationSpec = infiniteRepeatable(
            animation = tween(4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale1"
    )

    // Second circle animation: shrinks from 1.2 to 1.0 scale
    // Different timing creates organic movement
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
        // Decorative circle 1 - Top left area
        // Indigo color with high blur for soft glow
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

        // Decorative circle 2 - Bottom right area
        // Purple color creates complementary visual balance
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
 * ContentState - Enum representing possible screen states
 *
 * Used by AnimatedContent to switch between different UI layouts:
 * - Loading: Shows progress indicator while fetching data
 * - Error: Displays error message when operations fail
 * - Cards: Shows the list of drawn cards
 * - Empty: Initial state with action buttons
 */
private enum class ContentState {
    Loading, Error, Cards, Empty
}

// ========================================
// PREVIEW FUNCTIONS
// ========================================

/**
 * Preview with sample cards to test the main layout
 *
 * Shows the screen with 3 cards drawn and 49 remaining,
 * demonstrating the full UI with data.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeckGameScreenPreview() {
    BreweryTheme {
        // Sample card data for preview
        val sampleCards = listOf(
            CardDto(
                code = "AS",
                image = "https://deckofcardsapi.com/static/img/AS.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/AS.svg",
                    png = "https://deckofcardsapi.com/static/img/AS.png"
                ),
                value = "ACE",
                suit = "SPADES"
            ),
            CardDto(
                code = "KH",
                image = "https://deckofcardsapi.com/static/img/KH.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/KH.svg",
                    png = "https://deckofcardsapi.com/static/img/KH.png"
                ),
                value = "KING",
                suit = "HEARTS"
            ),
            CardDto(
                code = "QD",
                image = "https://deckofcardsapi.com/static/img/QD.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/QD.svg",
                    png = "https://deckofcardsapi.com/static/img/QD.png"
                ),
                value = "QUEEN",
                suit = "DIAMONDS"
            )
        )

        Scaffold(
            topBar = {
                ModernTopBar(
                    cardCount = sampleCards.size,
                    remainingCards = 49
                )
            },
            containerColor = Color.Transparent,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B),
                                    Color(0xFF334155)
                                )
                            )
                        )
                ) {
                    AnimatedBackground()

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        CardList(cards = sampleCards, onDrawMore = {})
                    }
                }
            }
        )
    }
}

/**
 * Preview of empty state before any deck is created
 *
 * Shows the initial screen with action buttons,
 * useful for testing the onboarding experience.
 */
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DeckGameScreenEmptyPreview() {
    BreweryTheme {
        Scaffold(
            topBar = {
                ModernTopBar(cardCount = 0, remainingCards = 0)
            },
            containerColor = Color.Transparent,
            content = { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF0F172A),
                                    Color(0xFF1E293B),
                                    Color(0xFF334155)
                                )
                            )
                        )
                ) {
                    AnimatedBackground()

                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxSize()
                    ) {
                        // ActionButtons component would be displayed here
                        // Placeholder for preview purposes
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Action Buttons go here",
                                color = Color.White.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        )
    }
}