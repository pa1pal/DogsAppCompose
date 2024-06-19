package io.pawan.dogsappcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableListDetailPaneScaffold
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
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
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.pawan.dogsappcompose.data.ApiResponse
import io.pawan.dogsappcompose.ui.theme.DogsAppComposeTheme
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
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

                        val navController = rememberNavController()

                        val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

                        BackHandler(navigator.canNavigateBack()) {
                            navigator.navigateBack()
                        }

                        NavigableListDetailPaneScaffold(
                            modifier = Modifier,
                            navigator = navigator,
                            defaultBackBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
                            listPane = {
                                DogsList(
                                    mainViewModel = mainViewModel
                                ) { dogBreed ->
                                    navigator.navigateTo(
                                        pane = ListDetailPaneScaffoldRole.Detail,
                                        content = dogBreed
                                    )
//                                    navController.navigate(Screen.DogInfoScreen(dogBreed))
                                }
                            },
                            detailPane = {
                                val dogBreed = navigator.currentDestination?.content ?: "affenpinscher"
                                DogInfo(mainViewModel, dogBreed.toString())
                            }
                        )

//                        NavHost(navController, startDestination = Screen.HomeScreen) {
//
//                            composable<Screen.HomeScreen> {
//                                DogsList(
//                                    mainViewModel = mainViewModel
//                                ) { dogBreed ->
//                                    navController.navigate(Screen.DogInfoScreen(dogBreed))
//                                }
//                            }
//
//                            composable<Screen.DogInfoScreen> { backStackEntry ->
//                                val dogBreed = backStackEntry.toRoute<Screen.DogInfoScreen>()
//                                requireNotNull(dogBreed) { "dogId parameter wasn't found. Please make sure it's set!" }
//                                // TODO: Fix as this is getting called on backpress as well
////                                if ( backStackEntry.maxLifecycle == Lifecycle.State.STARTED) {
//
////                                }
//                                DogInfo(mainViewModel, dogBreed.breedName)
//                            }
//                        }
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