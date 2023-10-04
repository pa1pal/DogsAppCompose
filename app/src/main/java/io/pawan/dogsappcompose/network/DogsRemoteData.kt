package io.pawan.dogsappcompose.network

import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import retrofit2.Response
import javax.inject.Inject

class DogsRemoteData @Inject constructor(
    private val apiService: ApiService
): RemoteDataSource {
    override suspend fun getAllDogsBreed(): Response<Breed> {
        return apiService.allDogsBreed()
    }

    override suspend fun getDogBreedImages(breed: String): Response<BreedImages> {
        return apiService.breedImages(breed)
    }
}