package com.sysco.starwars.ui

import com.sysco.starwars.data.model.Planet

sealed class ViewState {
    class Loading(val currentPage : Int) : ViewState()
    class Success(val planetList: List<Planet>) : ViewState()
    class Error(val error : String) : ViewState()
}