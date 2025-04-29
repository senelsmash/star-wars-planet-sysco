package com.sysco.starwars.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sysco.starwars.data.model.Planet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParallaxDetailScreen(
    planet: Planet,
    onBack: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val imageHeight = 300.dp
    val imageHeightPx = with(LocalDensity.current) { imageHeight.toPx() }
    val description = buildString {
        append("Welcome to the planetary profile of ${planet.name ?: "an unidentified celestial body"}.\n\n")
        append("This planet is known for its unique climate characteristics. With a general climate described as ${planet.climate ?: "unknown"}, the environmental conditions are shaped by its atmospheric composition, solar distance, and axial tilt. Such climate often influences not only the ecosystem but also the potential habitability and geological features of the planet.\n\n")
        append("The orbital period of this planet is approximately ${planet.orbitalPeriod ?: "an undetermined number of"} days. This means it takes that long to complete a full revolution around its star, which can significantly affect seasonal cycles and solar exposure. Depending on its axial tilt, residents—if any—may experience long days and nights or relatively balanced daylight hours.\n\n")
        append("In terms of rotation, the planet spins on its axis every ${planet.rotationPeriod ?: "unknown"} hours. A short rotation period typically results in more dynamic weather patterns due to faster atmospheric movement, while longer rotations could mean prolonged periods of daylight and darkness.\n\n")
        append("The surface gravity of the planet is estimated to be ${planet.gravity ?: "not recorded"}, which plays a crucial role in determining the physical characteristics of its terrain, water behavior, and even the physiology of any life forms that may exist there. For reference, Earth's gravity is considered '1 standard'.\n\n")
        append("Altogether, ${planet.name ?: "this planet"} stands out as a remarkable body in the known galaxy, with intriguing conditions worth deeper exploration. Scientists and explorers alike would find its climate and gravitational profile particularly fascinating for theoretical modeling, colonization possibilities, or even tourism in the far future.\n\n")
        append("Stay tuned for more interstellar data as we continue charting the stars and uncovering the secrets of distant worlds.")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = planet.name?: "unidentified celestial body", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = Color.Black.copy(alpha = 0.7f)
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                state = scrollState,
                contentPadding = PaddingValues(
                    top = imageHeight + paddingValues.calculateTopPadding()
                ),
                modifier = Modifier.fillMaxSize()
            ) {
                item {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "About ${planet.name?: "unidentified celestial body"}",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

            val offset = minOf(
                scrollState.firstVisibleItemScrollOffset.toFloat(),
                imageHeightPx
            )

            AsyncImage(
                model = planet.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(imageHeight)
                    .fillMaxWidth()
                    .graphicsLayer {
                        translationY = -offset / 2
                    }
            )
        }
    }
}
