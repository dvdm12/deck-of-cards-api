package com.eam.brewery

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.eam.brewery.ui.theme.BreweryTheme
import com.eam.brewery.viewmodel.ohttp.PokeViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BreweryTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    TestPokeApi(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

/**
 * Composable that triggers the API call once and displays the connection result.
 */
@Composable
fun TestPokeApi(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val pokeViewModel: PokeViewModel = viewModel()

    // Observes the message from the ViewModel
    val message by pokeViewModel.apiMessage

    // Launches the API test once
    LaunchedEffect(Unit) {
        pokeViewModel.testApi(context)
    }

    // UI
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(16.dp)
        )
    }
}
