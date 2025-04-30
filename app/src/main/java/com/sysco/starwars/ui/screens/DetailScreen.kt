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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.sysco.starwars.data.model.entity.Planet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    planet: Planet,
    onBack: () -> Unit
) {
    val scrollState = rememberLazyListState()
    val imageHeight = 300.dp
    val imageHeightPx = with(LocalDensity.current) { imageHeight.toPx() }
    //dummy content
    val description: AnnotatedString = buildAnnotatedString {
        val boldStyle = SpanStyle(fontWeight = FontWeight.Bold)

        append("Welcome to the detailed planetary dossier of ")
        withStyle(boldStyle) { append(planet.name ?: "an unidentified celestial body") }
        append(".\n\n")

        append("This fascinating planet exhibits a distinct and noteworthy climate profile. With a general atmospheric condition described as ")
        withStyle(boldStyle) { append(planet.climate ?: "undocumented") }
        append(", scientists believe that such a climate is shaped by a complex interplay of factors.\n\n")

        append("The orbital period of this celestial body is approximately ")
        withStyle(boldStyle) { append(planet.orbitalPeriod ?: "an unknown number of") }
        append(" days. This governs the length of a planetary year.\n\n")

        append("When it comes to daily cycles, the planet completes one full rotation on its axis every ")
        withStyle(boldStyle) { append(planet.rotationPeriod ?: "undetermined number of") }
        append(" hours, influencing atmospheric dynamics and temperature distribution.\n\n")

        append("Gravity on the surface is measured at ")
        withStyle(boldStyle) { append(planet.gravity ?: "an unverified value") }
        append(", affecting everything from fluid behavior to potential biological evolution.\n\n")

        append("Altogether, ")
        withStyle(boldStyle) { append(planet.name ?: "this planet") }
        append(" stands as a fascinating subject for deeper scientific exploration and theoretical modeling.")
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
