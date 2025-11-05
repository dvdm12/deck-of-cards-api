package com.eam.brewery.viewmodel.ohttp

import android.content.Context
import com.eam.brewery.R
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton that provides a dynamic Retrofit instance.
 *
 * The base URL is read from strings.xml, allowing flexible
 * configuration across build variants (e.g., dev, staging, prod).
 */
object RetrofitClient {

    // Creates a logging interceptor to monitor HTTP requests and responses
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // Builds the OkHttp client with defined timeouts and the logging interceptor
    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * Creates a Retrofit instance using the base URL defined in strings.xml.
     * The context is used to dynamically read the string resource.
     */
    fun getInstance(context: Context): Retrofit {
        val baseUrl = context.getString(R.string.base_url)
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}
