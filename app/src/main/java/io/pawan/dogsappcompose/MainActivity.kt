package io.pawan.dogsappcompose

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import io.branch.referral.Branch
import io.pawan.dogsappcompose.data.ApiResponse
import io.pawan.dogsappcompose.ui.theme.DogsAppComposeTheme
import io.pawan.dogsappcompose.utils.Constants
import java.util.Locale

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    val mainViewModel: MainViewModel by viewModels()
    var branchValueSet: Boolean = false


    companion object {
        private const val TAG = "MainActivity"
    }

    @SuppressLint("UnusedMaterialScaffoldPaddingParameter")
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
                    ) {
                        val mainViewModel = hiltViewModel<MainViewModel>()
                        val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
                        val isLandscape =
                            LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
                        val isBranchValueSet by mainViewModel.branchValueSet.collectAsState()

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
                                if (isBranchValueSet.not()) {
                                    val dogBreed =
                                        navigator.currentDestination?.content ?: "affenpinscher"
                                    mainViewModel.updateSelectedBreedName(dogBreed.toString())
                                    DogInfo(mainViewModel)
                                }
                            }
                        )

                        if (isBranchValueSet) {
                            if (isLandscape.not()) {
                                mainViewModel.updateBranchValue(false)
                                Log.d(TAG, "branch value toggled : $branchValueSet ")
                                navigator.navigateTo(
                                    pane = ListDetailPaneScaffoldRole.Detail,
                                    content = mainViewModel.selectedBreedName.value
                                )
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Branch.sessionBuilder(this).withCallback(branchReferralInitListener)
            .withData(this.intent.data).init()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra(
                "branch_force_new_session",
                false
            )
        ) {
            Log.i("onNewIntent", "Branch SDK reinitialized");
            Branch.sessionBuilder(this).withCallback(branchReferralInitListener).reInit();
        } else {
            Log.i("onNewIntent", "Intent is null");
        }
    }

    private val branchReferralInitListener =
        Branch.BranchReferralInitListener { referringParams, error -> // do stuff with deep link data (nav to page, display content, etc)
            if (error == null) {
                Log.i("BRANCH SDK logs", referringParams.toString())
                if (referringParams?.has(Constants.BREED_KEY) == true) {

                    Log.d(TAG, "branch value set : $branchValueSet ")
                    referringParams?.getString(Constants.BREED_KEY)
                        ?.let { mainViewModel.updateSelectedBreedName(it) }

                    mainViewModel.updateBranchValue(true)
                }
            } else {
                Log.e("BRANCH SDK", error.message)
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
    val defaultBreed: String = stringResource(R.string.default_breed)

//    LaunchedEffect(breedName) {
//        mainViewModel.fetchBreedDetails(breedName)
//    }

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
            var selectedItem by remember { mutableStateOf<String?>(mainViewModel.selectedBreedName.value) } // Tr
            val listState = rememberLazyListState()
            val isBranchValueSet by mainViewModel.branchValueSet.collectAsState()

            LaunchedEffect(selectedItem) {
                if (isBranchValueSet)
                    listState.animateScrollToItem(list.indexOf(selectedItem))
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 2.dp, vertical = 8.dp)
            ) {
                items(list, key = { it }) {
                    DogsListItem(
                        it,
                        isSelected = it == selectedItem,
                    ) { dogBreed ->
                        selectedItem = dogBreed
//                        mainViewModel.updateSelectedBreedName(dogBreed)
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
    isSelected: Boolean,
    handleClick: (String) -> Unit
) {
    val alataFontFamily = FontFamily(
        Font(R.font.alata_regular, FontWeight.Normal)
    )

    val isLandscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    val backgroundColor = if (isSelected && isLandscape) {
        MaterialTheme.colors.secondary // Or any color you want for highlighting
    } else {
        MaterialTheme.colors.surface
    }

    val elevation = if (isSelected && isLandscape) {
        0.dp // Or any color you want for highlighting
    } else {
        5.dp
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp)
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.medium,
        elevation = elevation,
        backgroundColor = backgroundColor,
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