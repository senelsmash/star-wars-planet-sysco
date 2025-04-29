package com.sysco.starwars

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.sysco.starwars.ui.screens.MainListScreen
import com.sysco.starwars.viewmodel.MainViewModel
import com.sysco.starwars.ui.theme.StarWarsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val mainViewModel : MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("Senel", "MainActivity onCreate")
        mainViewModel.getPlanetList()
        setContent {
            StarWarsTheme {
                MainListScreen(mainViewModel)
            }
        }
    }
}