package io.pawan.dogsappcompose

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import io.pawan.dogsappcompose.data.ApiResponse
import java.util.Locale

@Composable
fun DogInfo(
    mainViewModel: MainViewModel,
    breedName: String = ""
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val uiState = mainViewModel.breedDetailsState.collectAsState()
        var loading by remember { mutableStateOf(false) }
        val alataFontFamily = FontFamily(
            Font(R.font.alata_regular, FontWeight.Normal)
        )
        var breedName by remember { mutableStateOf(breedName) }

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
                Text(
                    modifier = Modifier
                        .padding(12.dp)
                        .wrapContentHeight(),
                    text = breedName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.ROOT) else it.toString() },
                    textAlign = TextAlign.Center,
                    fontSize = 48.sp,
                    fontFamily = alataFontFamily
                )


                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(uiState.value.data?.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = stringResource(
                        id = R.string.dog_image
                    ),
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                )
            }
        }
    }
}