package io.pawan.dogsappcompose.data

import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import kotlinx.coroutines.flow.Flow

interface RepoSource {
    suspend fun getAllDogsBreeds(): Flow<ApiResponse<Breed>>

    suspend fun getBreedImages(breed: String): Flow<ApiResponse<BreedImages>>

}