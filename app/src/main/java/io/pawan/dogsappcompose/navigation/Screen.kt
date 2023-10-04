package io.pawan.dogsappcompose.navigation

sealed class Screen(val route: String) {
    object Home: Screen("home")
    object DogInfo: Screen("dog/{breed}") {
        fun createRoute(dogId: String) = "dog/$dogId"
    }
}