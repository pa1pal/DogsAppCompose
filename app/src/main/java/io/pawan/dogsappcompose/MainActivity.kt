package io.pawan.dogsappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.pawan.dogsappcompose.data.ApiResponse
import io.pawan.dogsappcompose.navigation.Screen
import io.pawan.dogsappcompose.ui.theme.DogsAppComposeTheme
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DogsAppComposeTheme {
                // A surface container using the 'background' color from the theme
                val navController = rememberNavController()
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Scaffold(
                        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
                    ) { paddingValues ->
                        val mainViewModel = hiltViewModel<MainViewModel>()
                        NavHost(navController, startDestination = Screen.Home.route) {

                            composable(route = Screen.Home.route) {
                                DogsList(
                                    mainViewModel = mainViewModel
                                ) { dogBreed ->
                                    navController.navigate(Screen.DogInfo.createRoute(dogBreed))
                                }
                            }

                            composable(route = Screen.DogInfo.route) { backStackEntry ->

                                val dogBreed = backStackEntry.arguments?.getString("breed")
                                requireNotNull(dogBreed) { "dogId parameter wasn't found. Please make sure it's set!" }
                                // TODO: Fix as this is getting called on backpress as well
                                if ( backStackEntry.maxLifecycle == Lifecycle.State.STARTED) {
                                    mainViewModel.fetchBreedDetails(dogBreed)
                                }
                                DogInfo(mainViewModel, dogBreed)
                            }
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DogsList(
    mainViewModel: MainViewModel,
    showDogsDetails: (dogBreed: String) -> Unit
) {
    val uiState = mainViewModel.breedList.collectAsState()
    var loading by remember { mutableStateOf(false) }

    when (uiState.value) {

        is ApiResponse.Error -> {}
        is ApiResponse.Loading -> {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                IndeterminateCircularIndicator(loading = true)
            }
        }

        is ApiResponse.Success -> {
            loading = false
            val list = mutableListOf<String>()
            uiState.value.data?.message?.filterNotNull()?.let { list.addAll(it) }

            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
            ) {
                items(list) {
                    DogsListItem(it) { dogBreed ->
                        showDogsDetails.invoke(dogBreed)
                    }
                }
            }
        }
    }
}

@Composable
fun IndeterminateCircularIndicator(loading: Boolean) {
    if (!loading) return

    CircularProgressIndicator(
        modifier = Modifier.width(48.dp),
        color = MaterialTheme.colors.primary
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DogsListItem(
    breedName: String,
    handleClick: (String) -> Unit
) {
    val alataFontFamily = FontFamily(
        Font(R.font.alata_regular, FontWeight.Normal)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface,
        onClick = { handleClick(breedName) }
    ) {
        Text(
            modifier = Modifier
                .padding(12.dp)
                .wrapContentHeight(),
            text = breedName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
            textAlign = TextAlign.Center,
            fontFamily = alataFontFamily,
            fontSize = 22.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    DogsAppComposeTheme {
        DogsListItem("Pug") {

        }
    }
}