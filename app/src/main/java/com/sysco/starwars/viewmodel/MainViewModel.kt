package com.sysco.starwars.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sysco.starwars.data.model.entity.Planet
import com.sysco.starwars.data.repository.StarWarsRepository
import com.sysco.starwars.data.repository.StarWarsRepositoryImpl.Companion.toPlanet
import com.sysco.starwars.ui.ViewState
import com.sysco.starwars.util.ApiErrorState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

                val planetsWithImages = planetList.map { planet ->
                    async {
                        val imageUrl = starWarsRepository.getPicsumImage().getOrNull()
                        planet.copy(imageUrl = imageUrl.orEmpty())
                    }
                }.awaitAll()

                _viewState.value = ViewState.Success(planetsWithImages)
                nextPageUrl = response.nextPageUrl
                currentPage++
            }.onFailure { error ->
                errorHandler(error)
            }
        }
    }

    fun loadNextPage() {
        if (_isLoading.value || nextPageUrl == null) {
            //if already loading or next page is not available stop execution
            return
        }

        _isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val url = nextPageUrl!!

            val result = starWarsRepository.getPlanetPage(url)

            result.onSuccess { response ->
                val newPlanets = response.planetList.map { it.fields.toPlanet() }

                val updatedPlanets = newPlanets.map { planet ->
                    async {
                        val imageUrl = starWarsRepository.getPicsumImage().getOrNull()
                        planet.copy(imageUrl = imageUrl.orEmpty())
                    }
                }.awaitAll()

                val currentPlanets = (_viewState.value as? ViewState.Success)?.planetList.orEmpty()
                val combined = currentPlanets + updatedPlanets
                if(combined != currentPlanets) {
                    _viewState.value = ViewState.Success(combined)
                }
                nextPageUrl = response.nextPageUrl
            }.onFailure { error ->
                errorHandler(error)
                _viewState.value = ViewState.Error(error.message ?: "Unknown error")
            }
            _isLoading.value = false
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