package com.sysco.starwars.data.remote.datasource

import com.sysco.starwars.data.model.response.PlanetResponse

interface StarWarsApiDatasource {

    suspend fun getPlanetList(): PlanetResponse

    suspend fun getPlanetPage(pageUrl: String): PlanetResponse

    suspend fun getPicsumImage(): String

}