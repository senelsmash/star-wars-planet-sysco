package com.sysco.starwars.data.repository

import android.util.Log
import com.sysco.starwars.data.model.Planet
import com.sysco.starwars.data.model.response.PlanetData
import com.sysco.starwars.data.model.response.PlanetResponse
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
) : StarWarsRepository {
    private var nextPageUrl : String? = null

    override suspend fun getPlanetList(): Result<PlanetResponse> {
        return try {
            Log.d("senel","getPlanetList repo")
            val response = apiDataSource.getPlanetList()
            nextPageUrl = response.nextPageUrl
            Log.d("senel","getPlanetList repo " + response.planetList.size)
            Result.success(response)
        } catch (e : ApiErrorState.NoNetworkErrorState) {
            //TODO implement local data loading
            Result.failure(e)
        } catch (e : ApiErrorState.DataSourceErrorState) {
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

        fun Planet.toPlanetData() : PlanetData {
            return PlanetData(
                name = name,
                climate = climate,
                orbitalPeriod = orbitalPeriod,
                gravity = gravity,
                rotationPeriod = rotationPeriod
            )
        }
    }
}