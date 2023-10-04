package io.pawan.dogsappcompose.data

import android.util.Log
import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import io.pawan.dogsappcompose.network.DogsRemoteData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext

class MainRepository @Inject constructor(
    private val remoteData: DogsRemoteData,
    private val ioDispatcher: CoroutineContext
): RepoSource {
    override suspend fun getAllDogsBreeds(): Flow<ApiResponse<Breed>> {
        return flow {
            try {
                emit(ApiResponse.Loading())
                val response = remoteData.getAllDogsBreed()
                if (response.isSuccessful) {
                    emit(ApiResponse.Success(response.body() as Breed))
                } else {
                    emit(
                        ApiResponse.Error(
                            "Couldn't reach server. Check your internet connection. 1"
                        )
                    )
                }
            } catch (e: HttpException) {
                emit(
                    ApiResponse.Error(
                        e.localizedMessage ?: "An unexpected error occured"
                    )
                )
            } catch (e: IOException) {
                Log.e("TAG", "apiCallGetProviderLandingDetails: ${e.message}")
                emit(ApiResponse.Error("Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                Log.e("TAG", "apiCallGetProviderLandingDetails: ${e.message}")
                emit(ApiResponse.Error("Couldn't reach server. Check your internet connection."))
            }

        }.flowOn(ioDispatcher)
    }

    override suspend fun getBreedImages(breed: String): Flow<ApiResponse<BreedImages>> {
        return flow {
            try {
                emit(ApiResponse.Loading())
                val response = remoteData.getDogBreedImages(breed)
                if (response.isSuccessful) {
                    emit(ApiResponse.Success(response.body() as BreedImages))
                } else {
                    emit(
                        ApiResponse.Error(
                            "Couldn't reach server. Check your internet connection. 1"
                        )
                    )
                }
            } catch (e: HttpException) {
                emit(
                    ApiResponse.Error(
                        e.localizedMessage ?: "An unexpected error occured"
                    )
                )
            } catch (e: IOException) {
                Log.e("TAG", "apiCallGetProviderLandingDetails: ${e.message}")
                emit(ApiResponse.Error("Couldn't reach server. Check your internet connection."))
            } catch (e: Exception) {
                Log.e("TAG", "apiCallGetProviderLandingDetails: ${e.message}")
                emit(ApiResponse.Error("Couldn't reach server. Check your internet connection."))
            }

        }.flowOn(ioDispatcher)
    }
}