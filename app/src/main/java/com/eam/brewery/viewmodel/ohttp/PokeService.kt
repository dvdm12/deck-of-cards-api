package com.eam.brewery.viewmodel.ohttp

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Defines endpoints for testing the public PokeAPI.
 */
interface PokeService {

    // Example: https://pokeapi.co/api/v2/pokemon/
    @GET("{endpoint}/")
    suspend fun getEndpointData(
        @Path("endpoint") endpoint: String
    ): Response<Any>
}
