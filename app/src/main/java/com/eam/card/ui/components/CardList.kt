package com.eam.card.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eam.card.model.deck.CardDto
import com.eam.card.model.deck.CardImagesDto
import kotlinx.coroutines.launch

/**
 * CardList - Modern card display component with smooth animations
 *
 * Displays a grid of drawn cards with an elegant header
 * and a stylized "Draw More" button. Features smooth entrance animations and
 * a gradient background for visual appeal.
 *
 * @param cards List of cards to display
 * @param onDrawMore Callback triggered when user wants to draw more cards
 */
@Composable
fun CardList(cards: List<CardDto>, onDrawMore: () -> Unit) {
    // State management for smooth scrolling to newest cards
    val gridState = rememberLazyGridState()
    val coroutineScope = rememberCoroutineScope()

    // Animate the entire component entrance
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(600)) +
                slideInVertically(
                    initialOffsetY = { it / 3 },
                    animationSpec = tween(600, easing = FastOutSlowInEasing)
                )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp)
        ) {
            // Header section with card count and elegant typography
            HeaderSection(cardCount = cards.size)

            Spacer(modifier = Modifier.height(20.dp))

            // Grid scrollable card container with gradient background
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF1E293B).copy(alpha = 0.6f),
                tonalElevation = 4.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color(0xFF1E293B).copy(alpha = 0.4f),
                                    Color(0xFF334155).copy(alpha = 0.2f)
                                )
                            )
                        )
                ) {
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 140.dp),
                        state = gridState,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(24.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(
                            items = cards,
                            key = { card -> card.code }
                        ) { card ->
                            // Animate each card's entrance
                            AnimatedCardItem(card)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Modern "Draw More" button with gradient and icon
            DrawMoreButton(
                onClick = {
                    onDrawMore()
                    // Auto-scroll to show newest cards
                    coroutineScope.launch {
                        val lastIndex = cards.size - 1
                        if (lastIndex >= 0) {
                            gridState.animateScrollToItem(lastIndex)
                        }
                    }
                }
            )
        }
    }
}

/**
 * HeaderSection - Displays card count with animated counter
 *
 * Shows the number of drawn cards with a pulsing animation effect
 * to draw attention to the changing count.
 *
 * @param cardCount Current number of cards displayed
 */
@Composable
private fun HeaderSection(cardCount: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Your Hand",
                style = MaterialTheme.typography.titleSmall,
                color = Color.White.copy(alpha = 0.7f),
                fontWeight = FontWeight.Medium,
                letterSpacing = 1.2.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                // Animated card count
                AnimatedCardCount(count = cardCount)

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (cardCount == 1) "card" else "cards",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White.copy(alpha = 0.6f),
                    fontWeight = FontWeight.Normal
                )
            }
        }

        // Decorative gradient badge
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.Transparent,
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6366F1).copy(alpha = 0.3f),
                            Color(0xFF8B5CF6).copy(alpha = 0.3f)
                        )
                    ),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Text(
                text = "DRAWN",
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                letterSpacing = 1.5.sp
            )
        }
    }
}

/**
 * AnimatedCardCount - Number counter with scale animation
 *
 * Animates the card count number with a subtle scale effect
 * whenever the count changes.
 *
 * @param count The current card count to display
 */
@Composable
private fun AnimatedCardCount(count: Int) {
    var scale by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(count) {
        scale = 1.2f
        kotlinx.coroutines.delay(150)
        scale = 1f
    }

    val animatedScale by animateFloatAsState(
        targetValue = scale,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Text(
        text = count.toString(),
        style = MaterialTheme.typography.titleLarge,
        color = Color.White,
        fontWeight = FontWeight.Bold,
        fontSize = 32.sp,
        modifier = Modifier.scale(animatedScale)
    )
}

/**
 * AnimatedCardItem - Individual card with entrance animation
 *
 * Wraps each card with a fade and scale animation for smooth
 * appearance when cards are drawn.
 *
 * @param card The card data to display
 */
@Composable
private fun AnimatedCardItem(card: CardDto) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(card.code) {
        kotlinx.coroutines.delay(100)
        visible = true
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn(animationSpec = tween(400)) +
                scaleIn(
                    initialScale = 0.8f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
    ) {
        CardItem(card)
    }
}

/**
 * DrawMoreButton - Stylized action button with gradient and icon
 *
 * Modern button design with animated gradient background, icon rotation,
 * and smooth press animation. Features shimmer effect and elevated design.
 *
 * @param onClick Callback when button is pressed
 */
@Composable
private fun DrawMoreButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }

    // Scale animation for press effect
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "button_scale"
    )

    // Shimmer animation
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    val shimmerOffset by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_offset"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(68.dp)
            .scale(scale),
        shape = RoundedCornerShape(24.dp),
        shadowElevation = if (isPressed) 4.dp else 12.dp,
        tonalElevation = 8.dp
    ) {
        Button(
            onClick = {
                isPressed = true
                onClick()
            },
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(0.dp),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = 0.dp,
                pressedElevation = 0.dp
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF6366F1),
                                Color(0xFF8B5CF6),
                                Color(0xFFEC4899)
                            ),
                            start = androidx.compose.ui.geometry.Offset(
                                shimmerOffset * 1000f,
                                shimmerOffset * 1000f
                            ),
                            end = androidx.compose.ui.geometry.Offset(
                                shimmerOffset * 1000f + 1000f,
                                shimmerOffset * 1000f + 1000f
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Overlay shimmer effect
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color.White.copy(alpha = 0f),
                                    Color.White.copy(alpha = 0.3f),
                                    Color.White.copy(alpha = 0f)
                                ),
                                start = androidx.compose.ui.geometry.Offset(
                                    shimmerOffset * 800f - 200f,
                                    0f
                                ),
                                end = androidx.compose.ui.geometry.Offset(
                                    shimmerOffset * 800f + 200f,
                                    0f
                                )
                            )
                        )
                )

                Row(
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 24.dp)
                ) {
                    // Animated icon with rotation
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = Color.White.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier
                                .size(20.dp)
                                .scale(
                                    animateFloatAsState(
                                        targetValue = if (isPressed) 0.8f else 1f,
                                        label = "icon_scale"
                                    ).value
                                )
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(horizontalAlignment = Alignment.Start) {
                        Text(
                            text = "Draw More Cards",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Get lucky with new cards",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Normal,
                            letterSpacing = 0.3.sp
                        )
                    }
                }
            }
        }
    }

    // Reset press state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(150)
            isPressed = false
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0F172A)
@Composable
private fun CardListPreview() {
    MaterialTheme {
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
            ),
            CardDto(
                code = "JC",
                image = "https://deckofcardsapi.com/static/img/JC.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/JC.svg",
                    png = "https://deckofcardsapi.com/static/img/JC.png"
                ),
                value = "JACK",
                suit = "CLUBS"
            ),
            CardDto(
                code = "10S",
                image = "https://deckofcardsapi.com/static/img/0S.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/0S.svg",
                    png = "https://deckofcardsapi.com/static/img/0S.png"
                ),
                value = "10",
                suit = "SPADES"
            ),
            CardDto(
                code = "9H",
                image = "https://deckofcardsapi.com/static/img/9H.png",
                images = CardImagesDto(
                    svg = "https://deckofcardsapi.com/static/img/9H.svg",
                    png = "https://deckofcardsapi.com/static/img/9H.png"
                ),
                value = "9",
                suit = "HEARTS"
            )
        )

        CardList(
            cards = sampleCards,
            onDrawMore = {}
        )
    }
}