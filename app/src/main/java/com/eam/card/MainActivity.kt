package com.eam.card

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.eam.card.ui.screen.DeckGameScreen
import com.eam.card.ui.theme.BreweryTheme

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
