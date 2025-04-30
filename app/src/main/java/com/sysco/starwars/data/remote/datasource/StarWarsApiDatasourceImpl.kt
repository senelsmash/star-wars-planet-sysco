package com.sysco.starwars.data.remote.datasource

import android.util.Log
import com.sysco.starwars.data.model.response.PlanetResponse
import com.sysco.starwars.data.remote.PicsumApi
import com.sysco.starwars.data.remote.StarWarsApi
import com.sysco.starwars.util.ApiErrorState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withTimeout
import javax.inject.Inject

class StarWarsApiDatasourceImpl @Inject constructor(
    private val starWarsApi: StarWarsApi,
    private val picsumApi: PicsumApi
) : StarWarsApiDatasource {
    private val TAG: String = StarWarsApiDatasourceImpl::class.java.simpleName

    override suspend fun getPlanetList(): PlanetResponse {
        try {
            val planetListResponse = withTimeout(5000L) {
                starWarsApi.getPlanetList()
            }
            Log.d(TAG, "getPlanetList ---> ${planetListResponse.planetList.size}")
            return planetListResponse
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Timeout occurred while fetching planet list.")
            throw ApiErrorState.TimeOutErrorState("Request timed out.")
        } catch (e: Exception) {
            Log.e(TAG, "Error occurred: ${e.message}")
            throw ApiErrorState.from(e)
        }
    }

    override suspend fun getPlanetPage(pageUrl: String): PlanetResponse {
        try {
            val planetListResponse = withTimeout(5000L) {
                starWarsApi.getPlanetPage(pageUrl)
            }
            Log.d(TAG, "getPlanetPage ---> ${planetListResponse.planetList.size}")
            return planetListResponse
        } catch (e: TimeoutCancellationException) {
            Log.e(TAG, "Timeout occurred while fetching planet list.")
            throw ApiErrorState.TimeOutErrorState("Request timed out.")
        } catch (e: Exception) {
            throw ApiErrorState.from(e)
        }
    }

    override suspend fun getPicsumImage(): String {
        try {
            val response = picsumApi.getRedirectUrl()
            if (response.code() == 200) {
                val imageUrl = response.raw().request.url.toUrl().toString()
                Log.d(TAG, "getPicsumImage ---> ${imageUrl}")
                return imageUrl
            } else {
                return "" //TODO pass a locally stored placeholder uri
            }
        } catch (e: Exception) {
            throw ApiErrorState.from(e)
        }
    }
}