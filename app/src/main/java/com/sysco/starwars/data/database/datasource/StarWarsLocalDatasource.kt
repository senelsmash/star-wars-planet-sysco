package com.sysco.starwars.data.database.datasource

import com.sysco.starwars.data.model.entity.Planet

interface StarWarsLocalDatasource {
    suspend fun addPlanetList(planetList: List<Planet>)
    suspend fun getPlanetList(): List<Planet>
}