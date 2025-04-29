package com.sysco.starwars.data.remote

import android.util.Log
import com.sysco.starwars.data.database.StarWarsLocalDataSourceImpl
import com.sysco.starwars.data.model.response.PlanetResponse
import com.sysco.starwars.util.ApiErrorState
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

interface StarWarsApiDataSource {

    suspend fun getPlanetList(): PlanetResponse

    suspend fun getPlanetPage(pageUrl: String): PlanetResponse

    suspend fun getPicsumImage(): String

}


class StarWarsApiDataSourceImpl @Inject constructor(
    private val starWarsApi: StarWarsApi,
    private val picsumApi: PicsumApi
) : StarWarsApiDataSource {
    private val TAG: String = StarWarsLocalDataSourceImpl::class.java.simpleName

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