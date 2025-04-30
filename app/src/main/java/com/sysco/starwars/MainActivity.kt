package com.sysco.starwars

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.sysco.starwars.ui.screens.MainListScreen
import com.sysco.starwars.ui.theme.StarWarsTheme
import com.sysco.starwars.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black) // or any color
        window.decorView.systemUiVisibility = 0
        installSplashScreen()
        mainViewModel.getPlanetList()
        actionBar?.hide()
        setContent {
            StarWarsTheme {
                MainListScreen(mainViewModel)
            }
        }
    }
}