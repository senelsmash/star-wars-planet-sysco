package com.sysco.starwars.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sysco.starwars.data.model.Planet
import com.sysco.starwars.data.repository.StarWarsRepository
import com.sysco.starwars.data.repository.StarWarsRepositoryImpl.Companion.toPlanet
import com.sysco.starwars.ui.ViewState
import com.sysco.starwars.util.ApiErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val starWarsRepository: StarWarsRepository
) : ViewModel() {

    private var nextPageUrl: String? = null
    private var currentPage: Int = 1

    private val _viewState = MutableStateFlow<ViewState>(ViewState.Loading(currentPage))
    val viewState: StateFlow<ViewState> = _viewState.asStateFlow()

    private val _currentPlanet = MutableStateFlow<Planet?>(null)
    val currentPlanet: StateFlow<Planet?> = _currentPlanet.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun getPlanetList() {
        viewModelScope.launch(Dispatchers.IO) {
            _viewState.value = ViewState.Loading(currentPage)

            starWarsRepository.getPlanetList().onSuccess { response ->
                val planetList = response.planetList.map { it.fields.toPlanet() }
                _viewState.value = ViewState.Success(planetList)

                planetList.forEachIndexed { index, planet ->
                    viewModelScope.launch {
                        val imageUrl = starWarsRepository.getPicsumImage().getOrNull()
                        if (imageUrl != null) {
                            planet.imageUrl = imageUrl
                            updatePlanetImage(index, imageUrl)
                        }
                    }
                }

                nextPageUrl = response.nextPageUrl
                currentPage++
            }.onFailure { error ->
                errorHandler(error)
            }
        }
    }

    fun loadNextPage() {
        nextPageUrl?.let { url ->
            if (!_isLoading.value) {
                _isLoading.value = true

                viewModelScope.launch(Dispatchers.IO) {
                    starWarsRepository.getPlanetPage(url).onSuccess { response ->
                        val newPlanets = response.planetList.map { it.fields.toPlanet() }

                        val currentPlanets = (_viewState.value as? ViewState.Success)?.planetList.orEmpty()
                        val combined = currentPlanets + newPlanets
                        _viewState.value = ViewState.Success(combined)

                        newPlanets.forEachIndexed { index, planet ->
                            viewModelScope.launch {
                                val imageUrl = starWarsRepository.getPicsumImage().getOrNull()
                                if (imageUrl != null) {
                                    planet.imageUrl = imageUrl
                                    updatePlanetImage(currentPlanets.size + index, imageUrl)
                                }
                            }
                        }

                        nextPageUrl = response.nextPageUrl
                    }.onFailure { error ->
                        errorHandler(error)
                        _viewState.value = ViewState.Error(error.message ?: "Unknown error")
                    }

                    _isLoading.value = false
                }
            }
        } ?: run {
            _viewState.value = ViewState.Error("No more pages available.")
        }
    }

    private fun updatePlanetImage(index: Int, imageUrl: String) {
        val current = _viewState.value
        if (current is ViewState.Success) {
            val updated = current.planetList.toMutableList()
            val planet = updated[index].copy(imageUrl = imageUrl)
            updated[index] = planet
            _viewState.value = ViewState.Success(updated)
        }
    }

    fun onItemSelected(planet: Planet) {
        _currentPlanet.value = planet
    }

    fun onItemDetailsDismissed() {
        viewModelScope.launch {
            _currentPlanet.emit(null)
        }
    }

    private fun errorHandler(error: Throwable) {
        _viewState.value = when (error) {
            is ApiErrorState -> {
                ViewState.Error(error.message ?: "")
            }
            else -> {
                ViewState.Error("Unknown error occurred")
            }
        }
    }
}