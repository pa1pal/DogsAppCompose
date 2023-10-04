package io.pawan.dogsappcompose.network

import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("breeds/list")
    suspend fun allDogsBreed(): Response<Breed>

    @GET("breed/{breed}/images/random")
    suspend fun breedImages(@Path("breed") breed: String): Response<BreedImages>
}