package com.eam.card.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.eam.card.viewmodel.DeckViewModel
import com.eam.card.R

/**
 * ActionButtons - Primary user action interface for deck management
 *
 * Displays two main action buttons in the center of the screen:
 * 1. Create New Deck - Always enabled, initiates a new card deck
 * 2. Draw 5 Cards - Only enabled when a deck exists
 *
 * Features Material Design 3 elevated and tonal button styles
 * with appropriate disabled states and helper text.
 *
 * @param viewModel The DeckViewModel managing deck operations
 * @param context Android context needed for ViewModel operations
 * @param deckId Current deck identifier, null if no deck exists
 */
@Composable
fun ActionButtons(
    viewModel: DeckViewModel,
    context: android.content.Context,
    deckId: String?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_poker_casino_svgrepo_com),
            contentDescription = stringResource(R.string.poker_casino_icon),
            modifier = Modifier
                .size(166.dp)
                .padding(bottom = 24.dp),
            tint = Color.Unspecified
        )

        // Primary action button - Create new deck
        // Uses elevated style to stand out as the main action
        ElevatedButton(
            onClick = { viewModel.createNewDeck(context) },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Create New Deck",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Secondary action button - Draw cards
        // Uses tonal style for visual hierarchy below primary button
        // Only enabled when a deck exists (deckId != null)
        FilledTonalButton(
            onClick = { deckId?.let { viewModel.drawCards(context, it, count = 5) } },
            enabled = deckId != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = "Draw 5 Cards",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Helper text explaining why draw button is disabled
        // Only shown when no deck exists yet
        if (deckId == null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Create a deck first to draw cards",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White
            )
        }
    }
}

/**
 * Preview showing disabled state (no deck created yet)
 *
 * Demonstrates the initial user experience where only
 * the "Create New Deck" button is available and the
 * "Draw 5 Cards" button is disabled with helper text.
 */
@Preview(
    name = "No Deck - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFBFE
)
@Composable
private fun ActionButtonsPreviewNoDeck() {
    MaterialTheme {
        Surface {
            ActionButtonsPreview(deckId = null)
        }
    }
}

/**
 * Preview showing enabled state (deck exists)
 *
 * Shows both buttons active and ready for user interaction.
 * Helper text is not displayed in this state.
 */
@Preview(
    name = "With Deck - Light",
    showBackground = true,
    backgroundColor = 0xFFFFFBFE
)
@Composable
private fun ActionButtonsPreviewWithDeck() {
    MaterialTheme {
        Surface {
            ActionButtonsPreview(deckId = "sample-deck-123")
        }
    }
}

/**
 * ActionButtonsPreview - Preview helper component
 *
 * Standalone version without ViewModel dependencies,
 * allowing Android Studio to render previews properly.
 * Replicates the exact layout and styling of ActionButtons.
 *
 * @param deckId Sample deck ID for testing enabled/disabled states
 */
@Composable
private fun ActionButtonsPreview(deckId: String?) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Primary button - Create new deck
        ElevatedButton(
            onClick = { },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.elevatedButtonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            elevation = ButtonDefaults.elevatedButtonElevation(
                defaultElevation = 4.dp,
                pressedElevation = 8.dp
            )
        ) {
            Text(
                text = "Create New Deck",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Secondary button - Draw cards (state depends on deckId)
        FilledTonalButton(
            onClick = { },
            enabled = deckId != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.38f)
            )
        ) {
            Text(
                text = "Draw 5 Cards",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }

        // Helper text when no deck exists
        if (deckId == null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Create a deck first to draw cards",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primaryContainer
            )
        }
    }
}