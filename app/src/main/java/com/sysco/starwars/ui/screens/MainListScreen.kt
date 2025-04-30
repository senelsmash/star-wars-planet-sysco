package com.sysco.starwars.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.sysco.starwars.R
import com.sysco.starwars.data.model.entity.Planet
import com.sysco.starwars.ui.ViewState
import com.sysco.starwars.ui.components.CommonErrorMessage
import com.sysco.starwars.ui.components.loading.CommonLoadingIndicator
import com.sysco.starwars.ui.components.loading.LoadMoreLogic
import com.sysco.starwars.ui.components.loading.LoadMoreLogicImpl
import com.sysco.starwars.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainListScreen(
    viewModel: MainViewModel = hiltViewModel(),
    loadMoreLogic: LoadMoreLogic = LoadMoreLogicImpl()
) {
    val viewState by viewModel.viewState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val currentPlanet by viewModel.currentPlanet.collectAsState(initial = null)
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when (viewState) {
                is ViewState.Loading -> CommonLoadingIndicator()
                is ViewState.Success -> PlanetsList(
                    planetList = (viewState as ViewState.Success).planetList,
                    onPlanetSelected = viewModel::onItemSelected,
                    onLoadMore = viewModel::loadNextPage,
                    isLoadingMore = isLoading,
                    loadMoreLogic
                )

                is ViewState.Error -> CommonErrorMessage(
                    message = (viewState as ViewState.Error).error,
                    onRetry = viewModel::getPlanetList
                )
            }
            if (currentPlanet != null) {
                BackHandler {
                    viewModel.onItemDetailsDismissed()
                }
                DetailScreen(
                    currentPlanet!!,
                    viewModel::onItemDetailsDismissed
                )
            }
        }
    }
}

/**
 * Displays a lazy column of planet items with loading more functionality.
 */
@Composable
fun PlanetsList(
    planetList: List<Planet>,
    onPlanetSelected: (Planet) -> Unit,
    onLoadMore: () -> Unit,
    isLoadingMore: Boolean,
    loadMoreLogic: LoadMoreLogic = LoadMoreLogicImpl()
) {

    // Remembers the lazy list state to track the scroll position and load more items when needed
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        items(planetList) { planet ->
            PlanetItem(planet = planet, onPlanetSelected = onPlanetSelected)
        }

        if (isLoadingMore) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(24.dp)
                            .align(Alignment.Center)
                    )
                }
            }
        }
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo }
            .collectLatest { layoutInfo ->
                val shouldLoadMore = loadMoreLogic.shouldLoadMore(layoutInfo)
                if (shouldLoadMore) {
                    onLoadMore()
                }
            }
    }
}

/**
 * Displays a clickable card with a planet's name
 */
@Composable
fun PlanetItem(
    planet: Planet,
    onPlanetSelected: (Planet) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onPlanetSelected(planet) },
        elevation = 8.dp
    ) {
        Box {
            Image(
                painter = rememberAsyncImagePainter(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(planet.imageUrl)
                        .crossfade(true)
                        .size(300)
                        .build()
                ),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Gray),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .padding(16.dp)
            ) {
                Text(
                    text = planet.name
                        ?: stringResource(R.string.star_wars_na),
                    style = MaterialTheme.typography.h6,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.star_wars_climate_label, planet.climate
                        ?: stringResource(R.string.star_wars_na)),
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = stringResource(
                        R.string.star_wars_rotation_label,
                        planet.rotationPeriod
                            ?: stringResource(R.string.star_wars_na)
                    ),
                    style = MaterialTheme.typography.body2,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}