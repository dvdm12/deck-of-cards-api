package com.eam.brewery.viewmodel.ohttp

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Retrofit

class PokeViewModel : ViewModel() {

    // Global Retrofit and Service instances
    private lateinit var retrofit: Retrofit
    private lateinit var service: PokeService

    // State to expose message to the UI
    private val _apiMessage = mutableStateOf("Esperando conexión...")
    val apiMessage: State<String> = _apiMessage

    /**
     * Ensures Retrofit and PokeService are initialized once.
     */
    private fun ensureServiceInitialized(context: Context) {
        if (!::retrofit.isInitialized) {
            retrofit = RetrofitClient.getInstance(context)
            service = retrofit.create(PokeService::class.java)
            Log.d("PokeViewModel", "Retrofit and service initialized globally")
        }
    }

    /**
     * Performs an asynchronous API call using async/await.
     * Updates apiMessage for UI feedback.
     */
    fun testApi(context: Context) {
        Log.d("PokeViewModel", "Starting asynchronous API call...")

        ensureServiceInitialized(context)

        viewModelScope.launch {
            try {
                val deferredResponse = async { service.getEndpointData("pokemon") }
                val response = deferredResponse.await()

                if (response.isSuccessful) {
                    _apiMessage.value = "Conexión exitosa con la PokeAPI (Código ${response.code()})"
                    Log.d("PokeViewModel", "Pokemon request successful: ${response.code()}")
                    Log.d("PokeViewModel", "Pokemon body: ${response.body()}")
                } else {
                    _apiMessage.value = "Error en la conexión: ${response.code()} ${response.message()}"
                    Log.e("PokeViewModel", "Pokemon request failed: ${response.code()} ${response.message()}")
                }

                Log.d("PokeViewModel", "Asynchronous call completed")

            } catch (e: Exception) {
                _apiMessage.value = "Excepción: ${e.message}"
                Log.e("PokeViewModel", "Exception during async API call: ${e.message}", e)
            }
        }
    }
}
