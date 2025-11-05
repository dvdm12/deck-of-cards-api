package com.eam.card.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eam.card.model.deck.CardDto
import kotlinx.coroutines.launch

/**
 * CardList - Modern card display component with smooth animations
 *
 * Displays a horizontally scrollable list of drawn cards with an elegant header
 * and a stylized "Draw More" button. Features smooth entrance animations and
 * a gradient background for visual appeal.
 *
 * @param cards List of cards to display
 * @param onDrawMore Callback triggered when user wants to draw more cards
 */
@Composable
fun CardList(cards: List<CardDto>, onDrawMore: () -> Unit) {
    // State management for smooth scrolling to newest cards
    val listState = rememberLazyListState()
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

            // Horizontal scrollable card container with gradient background
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
                    LazyRow(
                        state = listState,
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
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

                    // Scroll indicator hint (visible when scrollable)
                    if (cards.size > 3) {
                        ScrollIndicator()
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
                        listState.animateScrollToItem(cards.size)
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
 * ScrollIndicator - Visual hint for horizontal scrolling
 *
 * Displays a pulsing arrow icon on the right edge to indicate
 * that more content is available by scrolling.
 */
@Composable
private fun BoxScope.ScrollIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "scroll_hint")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0.8f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Box(
        modifier = Modifier
            .align(Alignment.CenterEnd)
            .width(80.dp)
            .fillMaxHeight()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color(0xFF1E293B).copy(alpha = 0.8f)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBox,
            contentDescription = "Scroll to see more",
            tint = Color.White.copy(alpha = alpha),
            modifier = Modifier.size(32.dp)
        )
    }
}

/**
 * DrawMoreButton - Stylized action button with gradient and icon
 *
 * Modern button design with gradient background, icon, and smooth
 * press animation. Follows the app's color scheme for consistency.
 *
 * @param onClick Callback when button is pressed
 */
@Composable
private fun DrawMoreButton(onClick: () -> Unit) {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "button_scale"
    )

    Button(
        onClick = {
            isPressed = true
            onClick()
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .scale(scale),
        shape = RoundedCornerShape(20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 6.dp,
            pressedElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFF6366F1),
                            Color(0xFF8B5CF6)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "Draw More Cards",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }

    // Reset press state after animation
    LaunchedEffect(isPressed) {
        if (isPressed) {
            kotlinx.coroutines.delay(100)
        }
    }
}