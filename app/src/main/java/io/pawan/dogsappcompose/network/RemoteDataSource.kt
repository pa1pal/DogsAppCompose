package io.pawan.dogsappcompose.network

import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import retrofit2.Response

internal interface RemoteDataSource {
    suspend fun getAllDogsBreed(): Response<Breed>

    suspend fun getDogBreedImages(breed: String): Response<BreedImages>
}