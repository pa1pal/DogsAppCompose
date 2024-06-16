package io.pawan.dogsappcompose.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object HomeScreen: Screen()

    @Serializable
    data class DogInfoScreen(val breedName: String) : Screen()
}