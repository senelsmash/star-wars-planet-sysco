package com.sysco.starwars.data.repository

import android.util.Log
import com.sysco.starwars.data.database.StarWarsLocalDatasource
import com.sysco.starwars.data.model.Planet
import com.sysco.starwars.data.model.response.PlanetData
import com.sysco.starwars.data.model.response.PlanetResponse
import com.sysco.starwars.data.model.response.PlanetWrapper
import com.sysco.starwars.data.remote.StarWarsApiDataSource
import com.sysco.starwars.util.ApiErrorState
import javax.inject.Inject

interface StarWarsRepository {

    suspend fun getPlanetList() : Result<PlanetResponse>

    suspend fun getPlanetPage(pageUrl : String) : Result<PlanetResponse>

    suspend fun getPicsumImage() : Result<String>
}

class StarWarsRepositoryImpl @Inject constructor(
    private val apiDataSource : StarWarsApiDataSource,
    private val dbDataSource : StarWarsLocalDatasource
) : StarWarsRepository {

    private var nextPageUrl : String? = null

    override suspend fun getPlanetList(): Result<PlanetResponse> {
        return try {
            val response = apiDataSource.getPlanetList()
            nextPageUrl = response.nextPageUrl
            //add received list to database
            dbDataSource.addPlanetList(response.planetList.map { it.fields.toPlanet()})
            Result.success(response)
        } catch (e : ApiErrorState.NoNetworkErrorState) {
            //if any error occurred we will display using the database cached data
                val cachePlanetList = dbDataSource.getPlanetList().map {
                    //map to a response list type
                    it.toPlanetWrapper()
                }
                when {
                    cachePlanetList.isEmpty() -> {
                        Result.failure(e)
                    }
                    else -> {
                        val cachePlanetListRes = PlanetResponse(
                            count = cachePlanetList.size.toLong(),
                            nextPageUrl = nextPageUrl,
                            previous = null,
                            planetList = cachePlanetList
                        )
                        Result.success(cachePlanetListRes)
                    }
                }
        } catch (e : ApiErrorState) {
            Result.failure(e)
        }
    }

    override suspend fun getPlanetPage(pageUrl: String): Result<PlanetResponse> {
        return try {
            val response = apiDataSource.getPlanetPage(pageUrl)
            nextPageUrl = response.nextPageUrl
            Result.success(response)
        } catch (e : ApiErrorState) {
            Result.failure(e)
        }

    }

    override suspend fun getPicsumImage(): Result<String> {
        return try {
            val response = apiDataSource.getPicsumImage()
            Result.success(response)
        } catch (e : ApiErrorState) {
            Result.failure(e)
        }
    }

    companion object {
        //Mappers
        fun PlanetData.toPlanet() : Planet {
            return Planet(
                name = name,
                climate = climate,
                orbitalPeriod = orbitalPeriod,
                gravity = gravity,
                rotationPeriod = rotationPeriod,
                imageUrl = ""
            )
        }

        fun Planet.toPlanetWrapper() : PlanetWrapper {
            return PlanetWrapper(
                fields = PlanetData(
                    name = name,
                    climate = climate,
                    orbitalPeriod = orbitalPeriod,
                    gravity = gravity,
                    rotationPeriod = rotationPeriod
                )
            )
        }
    }
}