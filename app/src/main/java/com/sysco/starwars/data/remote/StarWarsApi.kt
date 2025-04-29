package com.sysco.starwars.data.remote

import com.sysco.starwars.data.model.response.PlanetResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface StarWarsApi {

    @GET("planets")
    suspend fun getPlanetList() : PlanetResponse

    @GET
    suspend fun getPlanetPage(@Url pageUrl : String) : PlanetResponse
}