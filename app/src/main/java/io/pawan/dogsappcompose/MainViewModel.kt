package io.pawan.dogsappcompose

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.pawan.dogsappcompose.data.ApiResponse
import io.pawan.dogsappcompose.data.MainRepository
import io.pawan.dogsappcompose.dto.Breed
import io.pawan.dogsappcompose.dto.BreedImages
import io.pawan.dogsappcompose.utils.NetworkHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository,
    private val networkHelper: NetworkHelper
) : ViewModel() {

    val breedList: StateFlow<ApiResponse<Breed>>
        get() = mutableBreedList
    private var mutableBreedList = MutableStateFlow<ApiResponse<Breed>>(ApiResponse.Loading(Breed()))

    val breedDetailsState: StateFlow<ApiResponse<BreedImages>>
        get() = mutableBreedDetailsState
    private var mutableBreedDetailsState = MutableStateFlow<ApiResponse<BreedImages>>(ApiResponse.Loading(BreedImages()))

    init {
        fetchBreedList()
    }

    fun fetchBreedList() {
        if (networkHelper.isConnected()) {
            viewModelScope.launch {
                mainRepository.getAllDogsBreeds().collect { res ->
                    mutableBreedList.update { res }
                }
            }
        } else {

        }
    }

    fun fetchBreedDetails(breedName: String) {
        if (networkHelper.isConnected()) {
            viewModelScope.launch {
                mainRepository.getBreedImages(breedName).collect { apiResponse ->

                    mutableBreedDetailsState.update {
                        apiResponse
                    }
                }
            }
        } else {
        }
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("compose", "onclear")
    }
}